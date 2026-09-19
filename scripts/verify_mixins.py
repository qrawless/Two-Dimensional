#!/usr/bin/env python3
"""Smoke test that validates the mod's Mixin setup.

It performs three independent checks:

1. Config consistency - every mixin listed in a mixins.json has a source file,
   and every ``@Mixin`` source file is registered in a config.
2. Target existence - every ``@Mixin(...)`` target class exists on the
   Minecraft / Sodium classpath (only run when the deobf jars are available).
3. Injection signatures - every ``method = "..."`` target resolves to a real
   method on the target (or one of its super types), catching descriptor drift
   when Minecraft updates.

Check 1 always runs. Checks 2 and 3 are skipped (with a warning) when the
deobfuscated jars or ``javap`` cannot be found, so the script still works on a
fresh checkout before ``gradle build`` has populated the cache.
"""

from __future__ import annotations

import argparse
import json
import os
import re
import shutil
import subprocess
import sys
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SRC = ROOT / "src"

CONFIGS = [
    (SRC / "main" / "resources" / "twodimensionalreloaded.mixins.json", "main", "mixins"),
    (SRC / "client" / "resources" / "twodimensionalreloaded.client.mixins.json", "client", "client"),
]

# Intentionally unregistered sources kept for reference.
INACTIVE_MIXIN_SOURCES = {
    Path("src/client/java/github/amvern/twodimensionalreloaded/client/mixin/LightEngineMixin.java"),
}

IMPORT_RE = re.compile(r"^\s*import\s+([\w.]+)\s*;\s*$", re.MULTILINE)
MIXIN_ANN_RE = re.compile(r"@Mixin\s*\(([^)]*)\)", re.DOTALL)
CLASS_REF_RE = re.compile(r"([\w.$]+)\.class")
STRING_REF_RE = re.compile(r'"([\w/.$]+)"')
METHOD_RE = re.compile(r'method\s*=\s*"([^"]+)"')
METHOD_SIG_RE = re.compile(r"^([\w$<>]+)(\(.*)")
PACKAGE_RE = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.MULTILINE)

errors: list[str] = []
warnings: list[str] = []


def rel(path: Path) -> str:
    return str(path.relative_to(ROOT)).replace("\\", "/")


def load_configs():
    configs = []
    for path, side, key in CONFIGS:
        if not path.exists():
            errors.append(f"missing mixin config: {rel(path)}")
            continue
        data = json.loads(path.read_text(encoding="utf-8"))
        configs.append((path, side, data.get("package", ""), list(data.get(key, []))))
    return configs


def source_candidates(side: str, package: str, name: str) -> list[Path]:
    """Possible .java paths for a registered mixin name.

    ``name`` may use a sub-package (``sodium.Foo``) or an inner class
    (``Outer.Inner``), which live at different locations.
    """
    base = SRC / side / "java" / Path(*package.split("."))
    segments = name.split(".")
    return [
        base / Path(*segments).with_suffix(".java"),  # sub-package form
        base / f"{segments[0]}.java",  # inner-class-in-outer-file form
    ]


def discover_mixin_sources() -> list[tuple[Path, str]]:
    found = []
    for path in SRC.rglob("*.java"):
        if "mixin" not in path.parts:
            continue
        text = path.read_text(encoding="utf-8", errors="replace")
        if "@Mixin" not in text:
            continue
        match = PACKAGE_RE.search(text)
        if match:
            found.append((path, f"{match.group(1)}.{path.stem}"))
    return found


def check_config_consistency():
    registered: dict[str, str] = {}
    for path, side, package, entries in load_configs():
        for entry in entries:
            fqcn = f"{package}.{entry}"
            if fqcn in registered:
                errors.append(f"{fqcn} is registered twice ({registered[fqcn]} and {path.name})")
            registered[fqcn] = path.name
            if not any(candidate.exists() for candidate in source_candidates(side, package, entry)):
                errors.append(f"{path.name}: registered mixin has no source file: {fqcn}")

    for src, fqcn in discover_mixin_sources():
        if Path(rel(src)) in INACTIVE_MIXIN_SOURCES:
            continue
        if fqcn in registered or any(name.startswith(f"{fqcn}.") for name in registered):
            continue
        errors.append(f"{rel(src)}: @Mixin source is not registered in any mixin config")


def resolve_imports(text: str) -> dict[str, str]:
    imports = {}
    for fqn in IMPORT_RE.findall(text):
        imports.setdefault(fqn.split(".")[-1], fqn)
    return imports


def resolve(expr: str, imports: dict[str, str]) -> str:
    if "." in expr:
        first, rest = expr.split(".", 1)
        return f"{imports[first]}.{rest}" if first in imports else expr
    return imports.get(expr, expr)


def mixin_targets(text: str) -> list[str]:
    imports = resolve_imports(text)
    targets = []
    for body in MIXIN_ANN_RE.findall(text):
        for expr in CLASS_REF_RE.findall(body):
            targets.append(resolve(expr, imports))
        for expr in STRING_REF_RE.findall(body):
            if not expr.startswith("/"):
                targets.append(expr.replace("/", "."))
    return targets


def binary_candidates(fqcn: str) -> list[str]:
    """Return binary-name variants (``Outer.Inner`` -> ``Outer$Inner``)."""
    parts = fqcn.split(".")
    variants = [fqcn]
    for index in range(1, len(parts)):
        if parts[index][:1].isupper():
            prefix = ".".join(parts[:index])
            inner = "$".join(parts[index:])
            variants.append(f"{prefix}.{inner}" if prefix else inner)
            break
    return variants


def project_minecraft_version() -> str:
    for line in (ROOT / "gradle.properties").read_text(encoding="utf-8").splitlines():
        if line.startswith("minecraft_version="):
            return line.split("=", 1)[1].strip()
    return ""


def find_classpath() -> list[Path]:
    gradle = Path(os.environ.get("GRADLE_USER_HOME", Path.home() / ".gradle"))
    mc = project_minecraft_version()
    jars = []
    for name in ("minecraft-common-deobf", "minecraft-clientonly-deobf"):
        jars += sorted(gradle.glob(f"caches/fabric-loom/minecraftMaven/net/minecraft/{name}/*/{name}-{mc}.jar"))
    jars += sorted(gradle.glob(f"caches/modules-2/files-2.1/net.caffeinemc/sodium-fabric/*/*/sodium-fabric-*+mc{mc}.jar"))
    return jars


def read_sources() -> list[tuple[Path, str]]:
    return [
        (path, path.read_text(encoding="utf-8", errors="replace"))
        for path in sorted(SRC.rglob("*.java"))
        if "mixin" in path.parts
    ]


def available_classes(classpath: list[Path]) -> set[str]:
    available = set()
    for jar in classpath:
        with zipfile.ZipFile(jar) as zf:
            available.update(name[:-6].replace("/", ".") for name in zf.namelist() if name.endswith(".class"))
    return available


def check_targets(classpath: list[Path]):
    available = available_classes(classpath)
    for path, text in read_sources():
        for target in mixin_targets(text):
            if not target.startswith(("net.minecraft", "net.caffeinemc")):
                continue
            if not any(variant in available for variant in binary_candidates(target)):
                errors.append(f"{rel(path)}: @Mixin target not found on classpath: {target}")


def signature_cache():
    javap = os.environ.get("JAVAP") or shutil.which("javap")
    if not javap:
        return None

    def make():
        cache: dict[str, dict] = {}

        def load(fqcn: str):
            if fqcn in cache:
                return cache[fqcn]
            simple = fqcn.split(".")[-1].split("$")[-1]
            output = ""
            for variant in binary_candidates(fqcn):
                try:
                    output = subprocess.run(
                        [javap, "-classpath", cache["classpath"], "-p", "-s", variant],
                        capture_output=True, text=True, timeout=60,
                    ).stdout
                except Exception:
                    output = ""
                if "Error:" not in output and output.strip():
                    break
            methods: set[str] = set()
            supers: list[str] = []
            pending = None
            for line in output.splitlines():
                header = re.match(r"^(?:public|protected|private|static|abstract|final|\s)*.*?\b([\w$]+)\s*\(", line)
                if header:
                    name = header.group(1)
                    if name.split("$")[-1] == simple:
                        name = "<init>"
                    pending = name
                    extends = re.search(r"\bextends\s+([\w.$]+)", line)
                    if extends:
                        supers.append(extends.group(1))
                    implements = re.search(r"\bimplements\s+([\w.$\s,]+)$", line)
                    if implements:
                        supers += [s.strip() for s in implements.group(1).split(",") if s.strip()]
                descriptor = re.match(r"^\s*descriptor:\s*(\S+)", line)
                if descriptor and pending:
                    methods.add(f"{pending} {descriptor.group(1)}")
                    pending = None
            cache[fqcn] = {"methods": methods, "supers": supers}
            return cache[fqcn]

        return cache, load

    return make


def check_signatures(classpath: list[Path]) -> bool:
    factory = signature_cache()
    if factory is None:
        return False
    cache, load = factory()
    cache["classpath"] = os.pathsep.join(str(j) for j in classpath)

    def has(fqcn: str, name: str, desc: str) -> bool:
        seen = set()
        queue = [fqcn]
        while queue:
            current = queue.pop(0)
            if not current or current in seen:
                continue
            seen.add(current)
            info = load(current)
            if f"{name} {desc}" in info["methods"]:
                return True
            queue.extend(info["supers"])
        return False

    checked = 0
    for path, text in read_sources():
        targets = [t for t in mixin_targets(text) if t.startswith(("net.minecraft", "net.caffeinemc"))]
        for sig in METHOD_RE.findall(text):
            match = METHOD_SIG_RE.match(sig)
            if not match:
                continue
            name, desc = match.group(1), match.group(2).replace(" ", "")
            if not desc.endswith(")") and not re.search(r"\)[\w\[/;$]+$", desc):
                continue
            for target in targets:
                checked += 1
                if not has(target, name, desc):
                    errors.append(f"{rel(path)}: unresolved injection target {target}::{name} {desc}")
    print(f"checked {checked} injection signatures")
    return True


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--skip-signatures", action="store_true", help="only run config and target checks")
    args = parser.parse_args()

    check_config_consistency()

    classpath = find_classpath()
    if classpath:
        check_targets(classpath)
        if not args.skip_signatures and not check_signatures(classpath):
            warnings.append("javap not found; skipped injection signature verification")
    else:
        warnings.append("deobf jars not found; skipped target and signature verification")

    for warning in warnings:
        print(f"warning: {warning}")
    if errors:
        print("\nmixin verification FAILED:")
        for error in errors:
            print(f"  - {error}")
        return 1

    print("mixin verification passed")
    return 0


if __name__ == "__main__":
    sys.exit(main())

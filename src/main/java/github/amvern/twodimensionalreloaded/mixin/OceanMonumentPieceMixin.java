package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OceanMonumentPieces.MonumentBuilding.class)
public abstract class OceanMonumentPieceMixin {

    @Inject(
        method = "<init>(Lnet/minecraft/util/RandomSource;IILnet/minecraft/core/Direction;)V",
        at = @At("RETURN")
    )
    private void centerOceanMonumentOnPlane(CallbackInfo ci) {
        OceanMonumentPieces.MonumentBuilding self = (OceanMonumentPieces.MonumentBuilding) (Object) this;
        int centerZ = self.getBoundingBox().minZ() + self.getBoundingBox().getZSpan() / 2;
        self.move(0, 0, -centerZ);
    }
}
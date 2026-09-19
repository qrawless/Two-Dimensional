package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.world.level.levelgen.structure.structures.OceanRuinPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OceanRuinPieces.OceanRuinPiece.class)
public abstract class OceanRuinPieceMixin {

    @Inject(
        method = "<init>(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/resources/Identifier;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Rotation;FLnet/minecraft/world/level/levelgen/structure/structures/OceanRuinStructure$Type;Z)V",
        at = @At("RETURN")
    )
    private void centerOceanRuinOnPlane(CallbackInfo ci) {
        OceanRuinPieces.OceanRuinPiece self = (OceanRuinPieces.OceanRuinPiece) (Object) this;
        int centerZ = self.getBoundingBox().minZ() + self.getBoundingBox().getZSpan() / 2;
        self.move(0, 0, -centerZ);
    }
}
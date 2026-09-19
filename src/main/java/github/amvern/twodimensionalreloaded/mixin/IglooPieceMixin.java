package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.world.level.levelgen.structure.structures.IglooPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IglooPieces.IglooPiece.class)
public abstract class IglooPieceMixin {

    @Inject(
        method = "<init>(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/resources/Identifier;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Rotation;I)V",
        at = @At("RETURN")
    )
    private void centerIglooOnPlane(CallbackInfo ci) {
        IglooPieces.IglooPiece self = (IglooPieces.IglooPiece) (Object) this;
        int centerZ = self.getBoundingBox().minZ() + self.getBoundingBox().getZSpan() / 2;
        self.move(0, 0, -centerZ);
    }
}
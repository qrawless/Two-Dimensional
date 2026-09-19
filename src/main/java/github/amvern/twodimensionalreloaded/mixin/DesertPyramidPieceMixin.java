package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.world.level.levelgen.structure.structures.DesertPyramidPiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DesertPyramidPiece.class)
public abstract class DesertPyramidPieceMixin {

    @Inject(
        method = "<init>(Lnet/minecraft/util/RandomSource;II)V",
        at = @At("RETURN")
    )
    private void centerDesertPyramidOnPlane(CallbackInfo ci) {
        DesertPyramidPiece self = (DesertPyramidPiece) (Object) this;
        int centerZ = self.getBoundingBox().minZ() + self.getBoundingBox().getZSpan() / 2;
        self.move(0, 0, -centerZ);
    }
}
package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.world.level.levelgen.structure.structures.JungleTemplePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JungleTemplePiece.class)
public abstract class JungleTemplePieceMixin {

    @Inject(
        method = "<init>(Lnet/minecraft/util/RandomSource;II)V",
        at = @At("RETURN")
    )
    private void centerJungleTempleOnPlane(CallbackInfo ci) {
        JungleTemplePiece self = (JungleTemplePiece) (Object) this;
        int centerZ = self.getBoundingBox().minZ() + self.getBoundingBox().getZSpan() / 2;
        self.move(0, 0, -centerZ);
    }
}
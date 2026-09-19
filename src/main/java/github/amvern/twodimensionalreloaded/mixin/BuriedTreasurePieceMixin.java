package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.world.level.levelgen.structure.structures.BuriedTreasurePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuriedTreasurePieces.BuriedTreasurePiece.class)
public abstract class BuriedTreasurePieceMixin {

    @Inject(
        method = "<init>(Lnet/minecraft/core/BlockPos;)V",
        at = @At("RETURN")
    )
    private void centerBuriedTreasureOnPlane(CallbackInfo ci) {
        BuriedTreasurePieces.BuriedTreasurePiece self = (BuriedTreasurePieces.BuriedTreasurePiece) (Object) this;
        int centerZ = self.getBoundingBox().minZ() + self.getBoundingBox().getZSpan() / 2;
        self.move(0, 0, -centerZ);
    }
}
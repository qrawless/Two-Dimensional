package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultifaceBlock.class)
public class MultifaceBlockMixin {

    @Inject(
        method = "canAttachTo(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void preventSouthAttach(BlockGetter level, Direction directionTowardsNeighbour, BlockPos neighbourPos, BlockState neighbourState, CallbackInfoReturnable<Boolean> cir) {
        if (directionTowardsNeighbour == Direction.NORTH) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "canAttachTo(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void preventSouthAttach(BlockGetter level, BlockPos pos, Direction directionTowardsNeighbour, CallbackInfoReturnable<Boolean> cir) {
        if (directionTowardsNeighbour == Direction.NORTH) {
            cir.setReturnValue(false);
        }
    }
}
package github.amvern.twodimensionalreloaded.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.BlockLightEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockLightEngine.class)
public abstract class BlockLightEngineMixin {
    @Shadow @Final private BlockPos.MutableBlockPos mutablePos;

    @WrapOperation(
        method = "propagateIncrease",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/lighting/BlockLightEngine;getOpacity(Lnet/minecraft/world/level/block/state/BlockState;)I"
        )
    )
    private int twodimensionalreloaded$blockLightGetOpacity(
        BlockLightEngine instance, BlockState blockState, Operation<Integer> original, @Local(name = "toNode") long toNode
    ) {
        if (Plane.isBackLayer(this.mutablePos)) {
            return this.mutablePos.getZ() < -1 ? 15 : 1;
        }

        return original.call(instance, blockState);
    }
}
package github.amvern.twodimensionalreloaded.client.mixin.sodium;

import github.amvern.twodimensionalreloaded.utils.Plane;
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DefaultFluidRenderer.class)
public class DefaultFluidRendererMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cullFluids(
            LevelSlice level, BlockState blockState, FluidState fluidState, BlockPos blockPos, BlockPos offset, TranslucentGeometryCollector collector, ChunkModelBuilder meshBuilder, Material material, ColorProvider<FluidState> colorProvider, FluidModel sprites, CallbackInfo ci
    ) {
        if (Plane.shouldCullRender(blockPos)) {
            ci.cancel();
        }
    }

    @Inject(method = "isFullBlockFluidSideVisible", at = @At("HEAD"), cancellable = true)
    private void enableCulledFluidSide(
            BlockGetter view, BlockPos selfPos, Direction facing, FluidState fluid, CallbackInfoReturnable<Boolean> cir
    ) {
        BlockPos pos = selfPos.relative(facing);
        if (Plane.shouldCullRender(pos)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isFullBlockFluidVisible", at = @At("HEAD"), cancellable = true)
    public void test(BlockAndTintGetter world, BlockPos pos, Direction dir, BlockState blockState, FluidState fluid, CallbackInfoReturnable<Boolean> cir) {
        BlockPos pos2 = pos.relative(dir);
        if (Plane.shouldCullRender(pos2)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isFluidSideExposed(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;F)Z", at = @At("HEAD"), cancellable = true)
    public void test2(BlockAndTintGetter world, BlockState ownBlockState, BlockPos neighborPos, Direction facing, float height, CallbackInfoReturnable<Boolean> cir) {
        BlockPos pos2 = neighborPos.relative(facing);
        if (Plane.shouldCullRender(pos2)) {
            cir.setReturnValue(true);
        }
    }
}
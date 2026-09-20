package github.amvern.twodimensionalreloaded.mixin;

import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGenMixin {

    private static final int SPACING = 16;

    @Inject(method = "applyBiomeDecoration", at = @At("RETURN"))
    private void onChunkGenerated(WorldGenLevel level, ChunkAccess chunk, net.minecraft.world.level.StructureManager structureManager, CallbackInfo ci) {
        if (chunk.getPos().getMinBlockZ() > Plane.FACE_FORWARD_LAYER_Z || chunk.getPos().getMaxBlockZ() < Plane.FACE_FORWARD_LAYER_Z) return;
        if (!chunk.getAllStarts().isEmpty()) return;

        int chunkX = chunk.getPos().x();
        int minX = chunk.getPos().getMinBlockX();
        int maxX = chunk.getPos().getMaxBlockX();
        int minY = chunk.getMinY();
        int maxY = chunk.getMinY() + chunk.getHeight();

        for (int ry = minY; ry < maxY; ry += SPACING) {
            int leftOffset = edgeOffset(chunkX, ry, 0);
            int rightOffset = edgeOffset(chunkX, ry, 1);
            int topOffset = edgeOffset(chunkX, ry, 2);
            int bottomOffset = edgeOffset(chunkX, ry, 3);

            removeBlock(chunk, new BlockPos(minX, ry + leftOffset, Plane.FACE_FORWARD_LAYER_Z));
            removeBlock(chunk, new BlockPos(maxX, ry + rightOffset, Plane.FACE_FORWARD_LAYER_Z));
            removeBlock(chunk, new BlockPos(minX + topOffset, ry, Plane.FACE_FORWARD_LAYER_Z));
            removeBlock(chunk, new BlockPos(minX + bottomOffset, ry + SPACING - 1, Plane.FACE_FORWARD_LAYER_Z));
        }
    }

    private static int edgeOffset(int chunkX, int ry, int edge) {
        int seed = chunkX * 31 + ry * 17 + edge * 13;
        return Math.floorMod(seed, SPACING);
    }

    private static void removeBlock(ChunkAccess chunk, BlockPos pos) {
        if (chunk.getBlockState(pos).is(Blocks.BEDROCK)) return;
        chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), 16);
    }
}
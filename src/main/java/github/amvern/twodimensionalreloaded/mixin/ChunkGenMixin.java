package github.amvern.twodimensionalreloaded.mixin;

import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGenMixin {

    private static final int SPACING = 16;

    private static final Set<Block> PROTECTED_BLOCKS = Set.of(
        Blocks.BEDROCK,
        Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE,
        Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE,
        Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE,
        Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE,
        Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE,
        Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE,
        Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE,
        Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE
    );

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
            removeBlock(chunk, new BlockPos(minX, ry + leftOffset + 1, Plane.FACE_FORWARD_LAYER_Z));
            removeBlock(chunk, new BlockPos(maxX, ry + rightOffset, Plane.FACE_FORWARD_LAYER_Z));
            removeBlock(chunk, new BlockPos(maxX, ry + rightOffset + 1, Plane.FACE_FORWARD_LAYER_Z));
            removeBlock(chunk, new BlockPos(minX + topOffset, ry, Plane.FACE_FORWARD_LAYER_Z));
            removeBlock(chunk, new BlockPos(minX + topOffset + 1, ry, Plane.FACE_FORWARD_LAYER_Z));
            removeBlock(chunk, new BlockPos(minX + bottomOffset, ry + SPACING - 1, Plane.FACE_FORWARD_LAYER_Z));
            removeBlock(chunk, new BlockPos(minX + bottomOffset + 1, ry + SPACING - 1, Plane.FACE_FORWARD_LAYER_Z));
        }
    }

    private static int edgeOffset(int chunkX, int ry, int edge) {
        int seed = chunkX * 31 + ry * 17 + edge * 13;
        return Math.floorMod(seed, SPACING - 1);
    }

    private static void removeBlock(ChunkAccess chunk, BlockPos pos) {
        if (pos.getY() >= surfaceY(chunk, pos)) return;
        BlockState state = chunk.getBlockState(pos);
        if (PROTECTED_BLOCKS.contains(state.getBlock())) return;
        chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), 16);
    }

    private static int surfaceY(ChunkAccess chunk, BlockPos pos) {
        int localX = pos.getX() - chunk.getPos().getMinBlockX();
        int localZ = pos.getZ() - chunk.getPos().getMinBlockZ();
        return chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, localX, localZ);
    }
}
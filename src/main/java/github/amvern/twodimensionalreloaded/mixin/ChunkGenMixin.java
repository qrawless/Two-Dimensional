package github.amvern.twodimensionalreloaded.mixin;

import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
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
        int minY = chunk.getMinY();
        int maxY = chunk.getMinY() + chunk.getHeight();

        for (int ry = minY; ry < maxY; ry += SPACING) {
            int seed = chunkX * 31 + ry * 17;
            RandomSource random = RandomSource.create(seed);
            int rx = random.nextInt(16);
            BlockPos blockPos = new BlockPos(chunk.getPos().getMinBlockX() + rx, ry, Plane.FACE_FORWARD_LAYER_Z);
            if (chunk.getBlockState(blockPos).is(Blocks.BEDROCK)) continue;
            chunk.setBlockState(blockPos, Blocks.AIR.defaultBlockState(), 16);
        }
    }
}
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

    @Inject(method = "applyBiomeDecoration", at = @At("RETURN"))
    private void onChunkGenerated(WorldGenLevel level, ChunkAccess chunk, net.minecraft.world.level.StructureManager structureManager, CallbackInfo ci) {
        if (chunk.getPos().getMinBlockZ() > Plane.FACE_FORWARD_LAYER_Z || chunk.getPos().getMaxBlockZ() < Plane.FACE_FORWARD_LAYER_Z) return;
        if (!chunk.getAllStarts().isEmpty()) return;
        int seed = chunk.getPos().x() * 31 + chunk.getPos().z() * 17;
        RandomSource random = RandomSource.create(seed);
        int rx = random.nextInt(16);
        int ry = 20 + random.nextInt(200);
        BlockPos blockPos = new BlockPos(chunk.getPos().getMinBlockX() + rx, ry, Plane.FACE_FORWARD_LAYER_Z);
        if (chunk.getBlockState(blockPos).is(Blocks.BEDROCK)) return;
        chunk.setBlockState(blockPos, Blocks.AIR.defaultBlockState(), 16);
    }
}

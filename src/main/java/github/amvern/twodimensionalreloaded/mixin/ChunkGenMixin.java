package github.amvern.twodimensionalreloaded.mixin;

import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.StructureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGenMixin {

    @Inject(method = "applyBiomeDecoration", at = @At("RETURN"))
    private void onChunkGenerated(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci) {
        ChunkPos pos = chunk.getPos();
        int minZ = pos.getMinBlockZ();
        int maxZ = pos.getMaxBlockZ();
        if (minZ > Plane.FACE_FORWARD_LAYER_Z || maxZ < Plane.FACE_FORWARD_LAYER_Z) return;
        RandomSource random = RandomSource.create();
        int rx = random.nextInt(16);
        int ry = random.nextInt(256);
        BlockPos blockPos = new BlockPos(pos.getMinBlockX() + rx, ry, Plane.FACE_FORWARD_LAYER_Z);
        chunk.setBlockState(blockPos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 16);
    }
}

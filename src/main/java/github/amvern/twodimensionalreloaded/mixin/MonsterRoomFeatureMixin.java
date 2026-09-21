package github.amvern.twodimensionalreloaded.mixin;

import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Puts the monster room (dungeon) spawner exactly on the playable base layer
 * so it always lands in the playable chunk at Z 0.
 *
 * <p>The room can only be shifted when the move keeps every written block
 * within the generated chunk's write radius. The room spans {@code z - (j+1)}
 * to {@code z + (j+1)} with {@code j} in 2..3, so centering on Z 0 touches only
 * chunks -1 and 0. That is safe for dungeons whose origin chunk is -1 or 0
 * (distance &le; 1), but a dungeon from chunk 1 would reach chunk -1 (distance
 * 2) and trip C2ME's unsafe-terrain-read guard, so those are left untouched.
 */
@Mixin(MonsterRoomFeature.class)
public abstract class MonsterRoomFeatureMixin {

    @ModifyVariable(method = "place", at = @At("HEAD"), argsOnly = true)
    private BlockPos centerMonsterRoomOnPlane(BlockPos original) {
        int chunkZ = Math.floorDiv(original.getZ(), 16);
        if (chunkZ == 0 || chunkZ == -1) {
            return new BlockPos(original.getX(), original.getY(), Plane.BASE_LAYER_Z);
        }
        return original;
    }
}
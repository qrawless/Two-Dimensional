package github.amvern.twodimensionalreloaded.mixin;

import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Centers the monster room (dungeon) on the playable plane so its mob spawner
 * is always reachable on the base layer.
 *
 * <p>The room is only shifted when the playable layer belongs to the chunk
 * currently being generated ({@code chunkZ} -1 or 0). Dungeons in any other
 * chunk are left untouched so that no block is written across chunks, which
 * would trip C2ME's unsafe-terrain-read guard.
 */
@Mixin(MonsterRoomFeature.class)
public abstract class MonsterRoomFeatureMixin {

    @ModifyVariable(method = "place", at = @At("HEAD"), argsOnly = true)
    private BlockPos centerMonsterRoomOnPlane(BlockPos original) {
        int chunkZ = Math.floorDiv(original.getZ(), 16);
        int layer;
        if (chunkZ == 0) {
            layer = Plane.BASE_LAYER_Z;
        } else if (chunkZ == -1) {
            layer = Plane.FACE_FORWARD_LAYER_Z;
        } else {
            return original;
        }
        return new BlockPos(original.getX(), original.getY(), layer);
    }
}
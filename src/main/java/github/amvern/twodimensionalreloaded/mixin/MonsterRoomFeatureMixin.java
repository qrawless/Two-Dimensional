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
 */
@Mixin(MonsterRoomFeature.class)
public abstract class MonsterRoomFeatureMixin {

    @ModifyVariable(method = "place", at = @At("HEAD"), argsOnly = true)
    private BlockPos centerMonsterRoomOnPlane(BlockPos original) {
        return new BlockPos(original.getX(), original.getY(), Plane.BASE_LAYER_Z);
    }
}
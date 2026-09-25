package github.amvern.twodimensionalreloaded.mixin;

import static github.amvern.twodimensionalreloaded.utils.Plane.PLANE_ENTITY_FLAG;

import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(SpawnUtil.class)
public class SpawnUtilMixin {

    @Redirect(
        method = "trySpawnMob",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;setWithOffset(Lnet/minecraft/core/Vec3i;III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
        )
    )
    private static BlockPos.MutableBlockPos restrictSpawnZToBaseLayer(BlockPos.MutableBlockPos mutable, Vec3i pos, int xOffset, int yOffset, int zOffset) {
        return mutable.setWithOffset(pos, xOffset, yOffset, Plane.BASE_LAYER_Z - pos.getZ());
    }

    @Redirect(
        method = "trySpawnMob",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;checkSpawnObstruction(Lnet/minecraft/world/level/LevelReader;)Z"
        )
    )
    private static boolean requireRoomOnBaseLayer(Mob mob, LevelReader level) {
        return Plane.hasRoomOnBaseLayer(level, mob.getBoundingBox());
    }

    @Inject(method = "trySpawnMob", at = @At("RETURN"))
    private static void adoptSummonedMobIntoPlane(EntityType<?> entityType, EntitySpawnReason reason, ServerLevel level, BlockPos pos, int attempts, int horizontalDistance, int yOffset, SpawnUtil.Strategy strategy, boolean checkCollision, CallbackInfoReturnable<Optional<? extends Mob>> cir) {
        cir.getReturnValue().ifPresent(spawned -> spawned.setAttached(PLANE_ENTITY_FLAG, true));
    }
}

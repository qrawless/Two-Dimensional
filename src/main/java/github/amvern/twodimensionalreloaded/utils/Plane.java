package github.amvern.twodimensionalreloaded.utils;

import com.mojang.serialization.Codec;
import github.amvern.twodimensionalreloaded.TwoDimensionalReloaded;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class Plane {
    private static final double CULL_DIST = -0.5;
    private static final double z = 0.5;
    private static boolean renderCulledBlocks = false;

    public static final int BASE_LAYER_Z = 0;
    public static final int FACE_AWAY_LAYER_Z = 1;
    public static final int FACE_FORWARD_LAYER_Z = -1;
    public static final int MAX_LAYER_Z = Math.max(FACE_AWAY_LAYER_Z, Math.abs(FACE_FORWARD_LAYER_Z));

    public static final double BLOCK_CENTER = 0.5;
    public static final double BLOCK_HALF_SIZE = 0.5;
    public static final double INTERACT_REACH = 1.8;
    public static final double CREATIVE_REACH = 5.0;
    public static final double SURVIVAL_REACH = 4.5;
    public static final float OPPOSITE_YAW = 180.0F;
    public static final int ENTITY_TRACK_RANGE_HORIZONTAL = 64;
    public static final int ENTITY_TRACK_RANGE_VERTICAL = 32;

    public Plane() {}

    public static double getZ() { return z; }
    public static int getIntZ() { return (int) Math.floor(z); }
    public static double getCullDist() { return CULL_DIST; }
    public static boolean isRenderingCulledBlocks() { return renderCulledBlocks; }
    public static void setRenderingCulledBlocks(boolean value) { renderCulledBlocks = value; }

    public static int layerZ(LayerMode mode) {
        return switch (mode) {
            case BASE -> BASE_LAYER_Z;
            case FACE_AWAY -> FACE_AWAY_LAYER_Z;
            case FACE_FORWARD -> FACE_FORWARD_LAYER_Z;
        };
    }

    public static boolean isLayerAllowed(BlockPos pos, LayerMode mode) {
        return pos.getZ() == layerZ(mode);
    }

    public static Vec3 intersectPoint(Vec3 point) {
        return new Vec3(point.x, point.y, z);
    }

    public static double sdf(Vec3 point) {
        return point.z - z;
    }

    public static boolean shouldCull(BlockPos blockPos) {
        if (renderCulledBlocks) {
            return blockPos.getZ() <= FACE_FORWARD_LAYER_Z - 1;
        }
        double dist = Plane.sdf(Vec3.atCenterOf(blockPos));
        return dist <= CULL_DIST;
    }

    public static boolean isBackLayer(BlockPos blockPos) {
        return blockPos.getZ() <= FACE_FORWARD_LAYER_Z;
    }

    public static boolean shouldInteract(BlockPos blockPos) {
        double dist = Plane.sdf(Vec3.atCenterOf(blockPos));
        return dist <= INTERACT_REACH;
    }

    public static boolean isWithinReach(Player player, BlockPos pos) {
        double range = player.isCreative() ? CREATIVE_REACH : SURVIVAL_REACH;
        Vec3 eye = player.getEyePosition();
        double dx = eye.x - (pos.getX() + BLOCK_CENTER);
        double dy = eye.y - (pos.getY() + BLOCK_CENTER);
        return dx * dx + dy * dy <= range * range;
    }

    @Override
    public String toString() {
        return "Plane{z= " + z + " }";
    }

    public static final AttachmentType<Boolean> PLANE_ENTITY_FLAG = AttachmentRegistry.create(
        Identifier.fromNamespaceAndPath(TwoDimensionalReloaded.MOD_ID, "is_on_plane"),
        builder -> builder
            .initializer(()-> false)
            .persistent(Codec.BOOL)
            .syncWith(
                ByteBufCodecs.BOOL,
                AttachmentSyncPredicate.all()
            )
            .copyOnDeath()
    );
}
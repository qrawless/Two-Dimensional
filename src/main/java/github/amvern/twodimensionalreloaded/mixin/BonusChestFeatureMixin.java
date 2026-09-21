package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.world.level.levelgen.feature.BonusChestFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Makes the bonus chest land on a playable layer: instead of scanning the full
 * Z span of its chunk it only tries the Z that belongs to the playable plane.
 * The chosen Z always stays within the chunk being generated (or a direct
 * neighbour), so C2ME's unsafe-terrain-read guard is not tripped.
 */
@Mixin(BonusChestFeature.class)
public abstract class BonusChestFeatureMixin {

    @ModifyArg(
        method = "place",
        at = @At(value = "INVOKE", ordinal = 1, target = "Ljava/util/stream/IntStream;rangeClosed(II)Ljava/util/stream/IntStream;"),
        index = 0
    )
    private int clampBonusChestMinZ(int minZ) {
        return accessibleLayer(minZ);
    }

    @ModifyArg(
        method = "place",
        at = @At(value = "INVOKE", ordinal = 1, target = "Ljava/util/stream/IntStream;rangeClosed(II)Ljava/util/stream/IntStream;"),
        index = 1
    )
    private int clampBonusChestMaxZ(int maxZ) {
        return accessibleLayer(maxZ);
    }

    private static int accessibleLayer(int blockZ) {
        int chunkZ = Math.floorDiv(blockZ, 16);
        if (chunkZ == 0) {
            return 0;
        }
        if (chunkZ == -1) {
            return -1;
        }
        if (chunkZ == 1) {
            return 0;
        }
        return blockZ;
    }
}
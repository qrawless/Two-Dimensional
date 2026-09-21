package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Recenters the whole mineshaft on the playable plane so its corridors,
 * supports and chest minecarts stay reachable instead of spreading across
 * many Z chunks.
 */
@Mixin(MineshaftStructure.class)
public abstract class MineshaftStructureMixin {

    @Inject(
        method = "generatePiecesAndAdjust(Lnet/minecraft/world/level/levelgen/structure/pieces/StructurePiecesBuilder;Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;)I",
        at = @At("RETURN")
    )
    private void centerMineshaftOnPlane(StructurePiecesBuilder builder, net.minecraft.world.level.levelgen.structure.Structure.GenerationContext context, CallbackInfoReturnable<Integer> cir) {
        List<StructurePiece> pieces = ((StructurePiecesBuilderAccessor) builder).getPieces();
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (StructurePiece piece : pieces) {
            minZ = Math.min(minZ, piece.getBoundingBox().minZ());
            maxZ = Math.max(maxZ, piece.getBoundingBox().maxZ());
        }
        int centerZ = (minZ + maxZ) / 2;
        for (StructurePiece piece : pieces) {
            piece.move(0, 0, -centerZ);
        }
    }
}
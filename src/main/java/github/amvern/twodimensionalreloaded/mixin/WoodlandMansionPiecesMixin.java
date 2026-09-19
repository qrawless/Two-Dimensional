package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WoodlandMansionPieces.class)
public abstract class WoodlandMansionPiecesMixin {

    @Inject(
        method = "generateMansion(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Rotation;Ljava/util/List;Lnet/minecraft/util/RandomSource;)V",
        at = @At("RETURN")
    )
    private static void centerWoodlandMansionOnPlane(StructureTemplateManager manager, BlockPos startPos, Rotation rotation, List<WoodlandMansionPieces.WoodlandMansionPiece> pieces, RandomSource random, CallbackInfo ci) {
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (WoodlandMansionPieces.WoodlandMansionPiece piece : pieces) {
            minZ = Math.min(minZ, piece.getBoundingBox().minZ());
            maxZ = Math.max(maxZ, piece.getBoundingBox().maxZ());
        }
        int centerZ = (minZ + maxZ) / 2;
        for (WoodlandMansionPieces.WoodlandMansionPiece piece : pieces) {
            piece.move(0, 0, -centerZ);
        }
    }
}
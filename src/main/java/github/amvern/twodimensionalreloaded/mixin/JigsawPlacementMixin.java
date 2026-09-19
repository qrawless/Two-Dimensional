package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(JigsawPlacement.class)
public abstract class JigsawPlacementMixin {

    @Redirect(
        method = "addPieces(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;Lnet/minecraft/core/Holder;Ljava/util/Optional;ILnet/minecraft/core/BlockPos;ZLjava/util/Optional;Lnet/minecraft/world/level/levelgen/structure/structures/JigsawStructure$MaxDistance;Lnet/minecraft/world/level/levelgen/structure/pools/alias/PoolAliasLookup;Lnet/minecraft/world/level/levelgen/structure/pools/DimensionPadding;Lnet/minecraft/world/level/levelgen/structure/templatesystem/LiquidSettings;)Ljava/util/Optional;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/structure/PoolElementStructurePiece;move(III)V"
        )
    )
    private static void centerStartPieceOnPlane(PoolElementStructurePiece piece, int x, int y, int z) {
        int centerZ = piece.getBoundingBox().minZ() + piece.getBoundingBox().getZSpan() / 2;
        piece.move(x, y, -centerZ);
    }
}
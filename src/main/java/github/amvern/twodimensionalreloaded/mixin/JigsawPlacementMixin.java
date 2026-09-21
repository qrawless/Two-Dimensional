package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Aligns every jigsaw-driven structure (village, bastion, ancient city, trial
 * chambers, trail ruins, pillager outpost) with the playable plane: the start
 * piece is centered while being placed, and once all pieces have been laid out
 * the whole structure is re-centered so its combined Z extent straddles Z 0.
 */
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

    @Inject(
        method = "addPieces(Lnet/minecraft/world/level/levelgen/RandomState;IZLnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/levelgen/structure/PoolElementStructurePiece;Ljava/util/List;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/level/levelgen/structure/pools/alias/PoolAliasLookup;Lnet/minecraft/world/level/levelgen/structure/templatesystem/LiquidSettings;)V",
        at = @At("RETURN")
    )
    private static void centerJigsawStructureOnPlane(
        RandomState randomState, int pieceCount, boolean requireTight, ChunkGenerator generator,
        StructureTemplateManager templateManager, LevelHeightAccessor heightAccessor, RandomSource random,
        Registry<StructureTemplatePool> pools, PoolElementStructurePiece startPiece,
        List<PoolElementStructurePiece> pieces, VoxelShape intersectsAtSelection,
        PoolAliasLookup aliasLookup, LiquidSettings liquidSettings, CallbackInfo ci
    ) {
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (PoolElementStructurePiece piece : pieces) {
            minZ = Math.min(minZ, piece.getBoundingBox().minZ());
            maxZ = Math.max(maxZ, piece.getBoundingBox().maxZ());
        }
        int centerZ = (minZ + maxZ) / 2;
        for (PoolElementStructurePiece piece : pieces) {
            piece.move(0, 0, -centerZ);
        }
    }
}
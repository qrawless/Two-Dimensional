package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.AbstractSpreadingStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.DimensionOriginStructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to restrict structure generation to +-3 chunks from z = 0.
 * In 26.3 StructurePlacement became an interface; the concrete implementations
 * of isStructureChunk live in these classes, so both are targeted.
 * */
@Mixin(value = {
        AbstractSpreadingStructurePlacement.class,
        DimensionOriginStructurePlacement.class
})
public abstract class StructurePlacementMixin {

    @Inject(method = "isStructureChunk", at = @At("HEAD"), cancellable = true)
    private void restrictStructuresToZ(ChunkGeneratorStructureState state, int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (Math.abs(chunkZ) > 3) {
            cir.setReturnValue(false);
        }
    }
}
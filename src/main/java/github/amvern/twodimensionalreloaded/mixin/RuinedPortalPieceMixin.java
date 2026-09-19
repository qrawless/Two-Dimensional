package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RuinedPortalPiece.class)
public abstract class RuinedPortalPieceMixin {

    @Inject(
        method = "<init>(Lnet/minecraft/core/HolderLookup$Provider;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/structures/RuinedPortalPiece$VerticalPlacement;Lnet/minecraft/world/level/levelgen/structure/structures/RuinedPortalPiece$Properties;Lnet/minecraft/resources/Identifier;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/world/level/block/Mirror;Lnet/minecraft/core/BlockPos;)V",
        at = @At("RETURN")
    )
    private void centerRuinedPortalOnPlane(CallbackInfo ci) {
        RuinedPortalPiece self = (RuinedPortalPiece) (Object) this;
        int centerZ = self.getBoundingBox().minZ() + self.getBoundingBox().getZSpan() / 2;
        self.move(0, 0, -centerZ);
    }
}
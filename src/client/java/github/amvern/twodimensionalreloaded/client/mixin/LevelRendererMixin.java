package github.amvern.twodimensionalreloaded.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "submitHitOutline", at = @At("HEAD"), cancellable = true)
    private void renderPlacementOutline(
            PoseStack poseStack, SubmitNodeCollector submitNodeCollector, RenderType renderType, net.minecraft.client.renderer.state.level.BlockOutlineRenderState state, int color, float width, boolean bl, CallbackInfo ci
    ) {
        BlockPos targetPos = state.pos();
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        if (Math.abs(targetPos.getZ()) > Plane.MAX_LAYER_Z || !Plane.isWithinReach(player, targetPos)) {
            ci.cancel();
        }
    }
}
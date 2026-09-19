package github.amvern.twodimensionalreloaded.client.mixin;

import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeatherEffectRenderer.class)
public class WeatherEffectRendererMixin {

    @Inject(
        method = "extractRenderState(Lnet/minecraft/client/multiplayer/ClientLevel;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/state/level/WeatherRenderState;)V",
        at = @At("RETURN")
    )
    private void cullBackLayerColumns(ClientLevel level, float f, Vec3 cameraPos, WeatherRenderState renderState, CallbackInfo ci) {
        renderState.rainColumns.removeIf(column -> column.z() <= Plane.FACE_FORWARD_LAYER_Z);
        renderState.snowColumns.removeIf(column -> column.z() <= Plane.FACE_FORWARD_LAYER_Z);
    }
}
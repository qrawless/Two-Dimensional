package github.amvern.twodimensionalreloaded.client.mixin.sodium;

import net.caffeinemc.mods.sodium.client.render.chunk.occlusion.OcclusionCuller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OcclusionCuller.class)
public class OcclusionCullerMixin {
    @Shadow private boolean useOcclusionCulling;

    @Inject(method = "findVisible", at = @At("HEAD"), remap = false)
    private void disableOcclusionCulling(CallbackInfo ci) {
        this.useOcclusionCulling = false;
    }
}
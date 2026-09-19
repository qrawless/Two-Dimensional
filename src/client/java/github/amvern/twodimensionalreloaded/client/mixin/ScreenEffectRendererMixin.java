package github.amvern.twodimensionalreloaded.client.mixin;

import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

    @ModifyVariable(method = "submit", at = @At("HEAD"), index = 5)
    private boolean modifyIsFirstPerson(boolean isFirstPerson) {
        return true;
    }

}
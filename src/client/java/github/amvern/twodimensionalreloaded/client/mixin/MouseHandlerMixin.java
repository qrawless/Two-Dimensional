package github.amvern.twodimensionalreloaded.client.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import github.amvern.twodimensionalreloaded.client.access.MouseNormalizedGetter;
import org.lwjgl.sdl.SDLMouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin implements MouseNormalizedGetter {
    @Shadow @Final private Minecraft minecraft;
    @Shadow private double xpos;
    @Shadow private double ypos;
    @Shadow private double accumulatedDX;
    @Shadow private double accumulatedDY;
    @Unique private Double twoDimensional$normalizedX = 0d;
    @Unique private Double twoDimensional$normalizedY = 0d;
    @Unique private double twoDimensional$virtualX = 0d;
    @Unique private double twoDimensional$virtualY = 0d;
    @Unique private boolean twoDimensional$recentering = false;

    @Override
    public double twoDimensional$getNormalizedX() {
        return Objects.requireNonNullElse(twoDimensional$normalizedX, 0d);
    }

    @Override
    public double twoDimensional$getNormalizedY() {
        return Objects.requireNonNullElse(twoDimensional$normalizedY, 0d);
    }

    @Override
    public double twoDimensional$getVirtualX() {
        return twoDimensional$virtualX;
    }

    @Override
    public double twoDimensional$getVirtualY() {
        return twoDimensional$virtualY;
    }

    @Unique
    private boolean twoDimensional$isInGame() {
        return this.minecraft.isWindowActive()
            && this.minecraft.gui.screen() == null
            && this.minecraft.gui.overlay() == null
            && this.minecraft.player != null;
    }

    @Inject(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;sensitivity()Lnet/minecraft/client/OptionInstance;"))
    public void updateNormalizedPos(CallbackInfo ci) {
        double width = this.minecraft.getWindow().getScreenWidth() / 2f;
        double height = this.minecraft.getWindow().getScreenHeight() / 2f;

        twoDimensional$normalizedX = (width - this.twoDimensional$virtualX) / width;
        twoDimensional$normalizedY = (height - this.twoDimensional$virtualY) / height;

        if (twoDimensional$normalizedX.isInfinite() || twoDimensional$normalizedX.isNaN()) {
            twoDimensional$normalizedX = 0d;
        }

        if (twoDimensional$normalizedY.isInfinite() || twoDimensional$normalizedY.isNaN()) {
            twoDimensional$normalizedY = 0d;
        }
    }

    @WrapWithCondition(method = "grabMouse", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/InputConstants;grabMouse(Lcom/mojang/blaze3d/platform/Window;DD)V"))
    public boolean grabMouse(Window window, double x, double y) {
        InputConstants.releaseMouse(window, x, y);
        SDLMouse.SDL_HideCursor();
        Window win = this.minecraft.getWindow();
        this.twoDimensional$virtualX = win.getScreenWidth() / 2d;
        this.twoDimensional$virtualY = win.getScreenHeight() / 2d;
        this.twoDimensional$recentering = false;
        return false;
    }

    @Inject(method = "releaseMouse", at = @At("HEAD"))
    public void releaseMouse(CallbackInfo ci) {
        SDLMouse.SDL_ShowCursor();
    }

    @Inject(method = "onMove(JDDDD)V", at = @At("HEAD"), cancellable = true)
    public void onVirtualMouseMoveHead(long window, double xNew, double yNew, double deltaX, double deltaY, CallbackInfo ci) {
        if (!this.twoDimensional$isInGame()) return;
        if (!this.twoDimensional$recentering) return;
        Window win = this.minecraft.getWindow();
        this.xpos = win.getScreenWidth() / 2d;
        this.ypos = win.getScreenHeight() / 2d;
        this.accumulatedDX = 0d;
        this.accumulatedDY = 0d;
        this.twoDimensional$recentering = false;
        ci.cancel();
    }

    @Inject(method = "onMove(JDDDD)V", at = @At("TAIL"))
    public void onVirtualMouseMoveTail(long window, double xNew, double yNew, double deltaX, double deltaY, CallbackInfo ci) {
        if (!this.twoDimensional$isInGame()) return;
        if (this.twoDimensional$recentering) return;
        Window win = this.minecraft.getWindow();
        this.twoDimensional$virtualX += deltaX;
        this.twoDimensional$virtualY += deltaY;
        float centerX = win.getScreenWidth() / 2f;
        float centerY = win.getScreenHeight() / 2f;
        SDLMouse.SDL_WarpMouseInWindow(win.handle(), centerX, centerY);
        this.xpos = centerX;
        this.ypos = centerY;
        this.twoDimensional$recentering = true;
    }
}
package github.amvern.twodimensionalreloaded.client.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LocalPlayer.class)
public interface LocalPlayerAccessor {
    @Invoker("raycastHitResult")
    HitResult invokeRaycastHitResult(float partialTicks, Entity cameraEntity);
}
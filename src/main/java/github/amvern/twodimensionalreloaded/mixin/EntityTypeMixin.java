package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public class EntityTypeMixin {

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void changeBoatDimensions(CallbackInfoReturnable<EntityDimensions> cir) {
        EntityType<?> type = (EntityType<?>)(Object)this;

        Identifier key = EntityType.getKey(type);
        if (key.getPath().endsWith("_boat") || key.getPath().endsWith("_raft")) {
            cir.setReturnValue(EntityDimensions.fixed(1.0F, 0.5625F));
        }
    }
}
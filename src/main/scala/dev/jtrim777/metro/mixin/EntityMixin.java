package dev.jtrim777.metro.mixin;

import dev.jtrim777.metro.entity.EntityAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
//    @Inject(method = "getStepHeight", at = @At(value = "TAIL"), cancellable = true)
//    public void getStepHeight(CallbackInfoReturnable<Float> retInfo) {
//        if (((Object)this) instanceof LivingEntity) {
//            float base = retInfo.getReturnValue();
//
//
//        }
//    }

    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;stepHeight:F", opcode = Opcodes.GETFIELD),
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;")
    public float getStepHeight(Entity instance) {
        if (instance instanceof LivingEntity) {
            float bonus = (float)((LivingEntity) instance).getAttributeInstance(EntityAttributes.StepHeightBonus()).getValue();
            return Math.max(0f, instance.stepHeight + bonus);
        } else {
            return instance.stepHeight;
        }
    }
}

package dev.jtrim777.metro.mixin;

import com.google.common.collect.Multimap;
import dev.jtrim777.metro.entity.ModEntityAttributes;
import dev.jtrim777.metro.entity.PlayerAttributesUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    protected LivingEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("RETURN"), method = "createLivingAttributes")
    private static void createLivingAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
        info.getReturnValue().add(ModEntityAttributes.StepHeightBonus());
    }

    @Inject(at = @At("RETURN"), method = "onEquipStack")
    public void onEquipStack(EquipmentSlot slot, ItemStack oldStack, ItemStack newStack, CallbackInfo ci) {
        if (((Entity) this) instanceof ServerPlayerEntity) {
            ServerPlayerEntity tgt = (ServerPlayerEntity) (Object) this;

            Multimap<EntityAttribute, EntityAttributeModifier> oldMods = oldStack.isEmpty() ? null : oldStack.getAttributeModifiers(slot);
            Multimap<EntityAttribute, EntityAttributeModifier> newMods = newStack.isEmpty() ? null : newStack.getAttributeModifiers(slot);

            PlayerAttributesUtil.syncUpdates(oldMods, newMods, tgt);
        }
    }
}

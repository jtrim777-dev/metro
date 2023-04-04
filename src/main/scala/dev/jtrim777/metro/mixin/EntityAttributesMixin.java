package dev.jtrim777.metro.mixin;

import dev.jtrim777.metro.entity.ModEntityAttributes;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityAttributes.class)
public abstract class EntityAttributesMixin {
    @Shadow
    private static EntityAttribute register(String id, EntityAttribute attribute) {
        throw new UnsupportedOperationException();
    }

    static {
        EntityAttribute shb = ModEntityAttributes.buildStepHeightBonus();
        register("metro:step_height_bonus", shb);
    }
}

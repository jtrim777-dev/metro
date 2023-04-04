package dev.jtrim777.metro.magic.enchants

import dev.jtrim777.metro.entity.ModEntityAttributes
import net.minecraft.enchantment.Enchantment.Rarity
import net.minecraft.enchantment.{Enchantment, EnchantmentTarget}
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation
import net.minecraft.entity.attribute.{EntityAttribute, EntityAttributeModifier, EntityAttributes}
import net.minecraft.item.ItemStack

import java.util.UUID

class CelerityEnchantment extends Enchantment(Rarity.RARE, EnchantmentTarget.ARMOR_FEET, Array(EquipmentSlot.FEET)) with ArmorEnchant {
  override def getMaxLevel: Int = 3
  override def isAvailableForEnchantedBookOffer: Boolean = false
  override def isAvailableForRandomSelection: Boolean = false

  override def getAttributeMods(slot: EquipmentSlot, stack: ItemStack, level: Int): Map[EntityAttribute, List[EntityAttributeModifier]] = {
    if (slot == EquipmentSlot.FEET) {
      val bonus: Double = 0.2 * level

      Map(
        EntityAttributes.GENERIC_MOVEMENT_SPEED -> List(
          new EntityAttributeModifier(CelerityEnchantment.ModID, "Celerity speed bonus", bonus, Operation.MULTIPLY_TOTAL)
        )
      )
    } else Map.empty
  }


}

object CelerityEnchantment {
  private val ModID = UUID.fromString("9fb1c5f7-a86c-4fe9-a95e-5fabb605f100")
}

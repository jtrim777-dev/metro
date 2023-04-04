package dev.jtrim777.metro.magic.enchants

import dev.jtrim777.metro.entity.ModEntityAttributes
import net.minecraft.enchantment.{Enchantment, EnchantmentTarget}
import net.minecraft.enchantment.Enchantment.Rarity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.{EntityAttribute, EntityAttributeModifier}
import EntityAttributeModifier.Operation
import net.minecraft.item.ItemStack

import java.util.UUID

class ElasticityEnchantment extends Enchantment(Rarity.RARE, EnchantmentTarget.ARMOR_FEET, Array(EquipmentSlot.FEET)) with ArmorEnchant {
  override def getMaxLevel: Int = 3
  override def isAvailableForEnchantedBookOffer: Boolean = false
  override def isAvailableForRandomSelection: Boolean = false

  override def getAttributeMods(slot: EquipmentSlot, stack: ItemStack, level: Int): Map[EntityAttribute, List[EntityAttributeModifier]] = {
    if (slot == EquipmentSlot.FEET) {
      val bonus: Double = 0.5 * level

      Map(
        ModEntityAttributes.StepHeightBonus -> List(
          new EntityAttributeModifier(ElasticityEnchantment.ModID, "Elasticity step-height bonus", bonus, Operation.ADDITION)
        )
      )
    } else Map.empty
  }


}

object ElasticityEnchantment {
  private val ModID = UUID.fromString("096c95fa-c4bb-4b7e-9681-5db61b9f960f")
}

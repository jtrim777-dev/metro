package dev.jtrim777.metro.magic.enchants

import com.google.common.collect.Multimap
import net.fabricmc.fabric.api.item.v1.ModifyItemAttributeModifiersCallback
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.{EntityAttribute, EntityAttributeModifier}
import net.minecraft.item.ItemStack
import scala.jdk.CollectionConverters._

trait ArmorEnchant {
  def getAttributeMods(slot: EquipmentSlot, stack: ItemStack, level: Int): Map[EntityAttribute, List[EntityAttributeModifier]]
}

object ArmorEnchant {
  private def applyEnchant(stack: ItemStack, slot: EquipmentSlot,
                           attributeModifiers: Multimap[EntityAttribute, EntityAttributeModifier]): Unit = {
    val enchants = EnchantmentHelper.get(stack).asScala.toList

    enchants.foreach {
      case (ae:ArmorEnchant, level) =>
        val mods = ae.getAttributeMods(slot, stack, level)
        mods.foreach { case (attr, amods) =>
          amods.foreach(m => attributeModifiers.put(attr, m))
        }
      case _ => ()
    }
  }

  def registerEventHandler(): Unit = {
    ModifyItemAttributeModifiersCallback.EVENT.register(applyEnchant)
  }
}

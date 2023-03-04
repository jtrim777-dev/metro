package dev.jtrim777.metro.item

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.{Item, ItemGroup, ItemStack}
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.World

import java.util

class Coin(val cubitValue: Int) extends Item(new FabricItemSettings().group(ItemGroup.MISC)) {
  override def appendTooltip(stack: ItemStack, world: World,
                             tooltip: util.List[Text], context: TooltipContext): Unit = {
    super.appendTooltip(stack, world, tooltip, context)

    val value = cubitValue * stack.getCount

    val baseText = if (value == 1) {
      Text.translatable("item.metro.coin.value_tooltip_single")
    } else Text.translatable("item.metro.coin.value_tooltip", value)

    val colored = baseText.formatted(Formatting.GRAY)

    tooltip.add(colored)
  }
}

object Coin {
  val CopperValue: Int = 1
  val IronValue: Int = 16
  val GoldValue: Int = 64
}

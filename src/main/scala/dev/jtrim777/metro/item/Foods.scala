package dev.jtrim777.metro.item

import net.minecraft.item.FoodComponent

object Foods {
  val Grape: FoodComponent = (new FoodComponent.Builder).hunger(1).saturationModifier(0.6F).build
}

package dev.jtrim777.metro.item

import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry
import dev.jtrim777.metro.ctx
import dev.jtrim777.needle.util.syntax.IdHelper


object ItemProperties {
  def register(): Unit = {
    registerOne("coin_pouch", "filled") { (is, _, _) =>
      CoinPouch.attemptLoad(is).map(d => if (d.storedValue > 0) 1f else 0f).getOrElse(0f)
    }
  }

  def registerOne(itemName: String, property: String)(provider: (ItemStack, ClientWorld, LivingEntity) => Float): Unit = {
    ModelPredicateProviderRegistry.register(Registry.ITEM.get(id"$itemName"), id"$property",
      (stack: ItemStack, world: ClientWorld, entity: LivingEntity, _: Int) => provider(stack, world, entity))
  }
}

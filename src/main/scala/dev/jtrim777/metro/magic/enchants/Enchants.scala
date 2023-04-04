package dev.jtrim777.metro.magic.enchants

import dev.jtrim777.metro.MetroMod
import dev.jtrim777.needle.registry.{DelayedRegistry, registered, registry}
import net.minecraft.enchantment.Enchantment
import net.minecraft.util.registry.Registry

@registry
object Enchants extends DelayedRegistry[Enchantment] {
  override def registry: Registry[Enchantment] = Registry.ENCHANTMENT
  override def namespace: String = MetroMod.ModID

  @registered lazy val Elasticity = new ElasticityEnchantment
  @registered lazy val Celerity = new CelerityEnchantment
}

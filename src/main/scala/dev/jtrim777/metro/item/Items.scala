package dev.jtrim777.metro.item

import dev.jtrim777.metro.MetroMod
import dev.jtrim777.needle.registry.{DelayedRegistry, registered}
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object Items extends DelayedRegistry[Item] {
  val registry: Registry[Item] = Registry.ITEM

  override def namespace: String = MetroMod.ModID

  def get(id: String): Item = Registry.ITEM.get(new Identifier(namespace, id))

  @registered lazy val CopperCoin: Item = new Coin(Coin.CopperValue)
  @registered lazy val IronCoin: Item = new Coin(Coin.IronValue)
  @registered lazy val GoldCoin: Item = new Coin(Coin.GoldValue)

  @registered lazy val CoinPouch: CoinPouch = new CoinPouch
}

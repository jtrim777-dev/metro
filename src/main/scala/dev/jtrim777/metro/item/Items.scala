package dev.jtrim777.metro.item

import dev.jtrim777.metro.MetroMod
import dev.jtrim777.metro.block.Blocks
import dev.jtrim777.needle.registry.{DelayedRegistry, registered, registry}
import net.minecraft.block.Block
import net.minecraft.item.{BlockItem, FoodComponent, Item, ItemGroup}
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

@registry object Items extends DelayedRegistry[Item] {
  val registry: Registry[Item] = Registry.ITEM

  override def namespace: String = MetroMod.ModID

  def get(id: String): Item = Registry.ITEM.get(new Identifier(namespace, id))
  def ofBlock(b: => Block, group: ItemGroup): Item = new BlockItem(b, (new Item.Settings).group(group))
  def food(component: FoodComponent): Item = new Item((new Item.Settings).food(component).group(ItemGroup.FOOD))

  @registered lazy val CopperCoin: Item = new Coin(Coin.CopperValue)
  @registered lazy val IronCoin: Item = new Coin(Coin.IronValue)
  @registered lazy val GoldCoin: Item = new Coin(Coin.GoldValue)

  @registered lazy val CoinPouch: CoinPouch = new CoinPouch

  @registered lazy val Grape: Item = food(Foods.Grape)
  @registered lazy val GrapeSeeds: StakedPlantSeed = StakedPlantSeed(Blocks.GrapeVineRoot)

  // Block items
  @registered lazy val Stake: Item = ofBlock(Blocks.Stake, ItemGroup.DECORATIONS)
}

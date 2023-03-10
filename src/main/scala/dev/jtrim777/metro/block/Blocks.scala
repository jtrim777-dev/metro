package dev.jtrim777.metro.block

import dev.jtrim777.metro.MetroMod
import dev.jtrim777.metro.item.Items
import dev.jtrim777.needle.registry.{DelayedRegistry, registered, registry}
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.block.Block
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

@registry object Blocks extends DelayedRegistry[Block] {
  val registry: Registry[Block] = Registry.BLOCK
  override def namespace: String = MetroMod.ModID

  @registered lazy val Stake: StakeBlock = new StakeBlock()

  private val (grapeRoot, grapeExt) = StakedPlant.create(3, new Identifier(namespace, "grape_seeds"),
    Items.Grape, 3, 5)
  @registered lazy val GrapeVineRoot: StakedPlant.Root = grapeRoot
  @registered lazy val GrapeVineExt: StakedPlant.Ext = grapeExt

  def registerAsCutout(block: Block): Unit = {
    BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout)
  }

  def onClientInit(): Unit = {
    registerAsCutout(GrapeVineRoot)
    registerAsCutout(GrapeVineExt)
  }
}

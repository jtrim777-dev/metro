package dev.jtrim777.metro.item

import dev.jtrim777.metro.block.{Blocks, StakeBlock, StakedPlant}
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.BlockState
import net.minecraft.item.{Item, ItemGroup, ItemUsageContext}
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

case class StakedPlantSeed(plant: StakedPlant.Root) extends Item(StakedPlantSeed.Properties) {
  override def useOnBlock(context: ItemUsageContext): ActionResult = {
    val world = context.getWorld

    val tgtPos = context.getBlockPos
    val tgt = world.getBlockState(tgtPos)

    if (canPlantOn(tgt, tgtPos, world)) {
      this.plant(tgtPos, world)
      context.getStack.decrement(1)
      ActionResult.success(world.isClient)
    } else {
      val tpup = tgtPos.up():BlockPos
      val tup = world.getBlockState(tpup)

      if (canPlantOn(tup, tpup, world)) {
        this.plant(tpup, world)
        context.getStack.decrement(1)
        ActionResult.success(world.isClient)
      }

      super.useOnBlock(context)
    }
  }

  private def canPlantOn(state: BlockState, pos: BlockPos, world: World): Boolean = {
    state.isOf(Blocks.Stake) && !state.get(StakeBlock.Waterlogged) && plant.isPositionValid(pos, world)
  }

  private def plant(pos: BlockPos, world: World): Unit = {
    world.setBlockState(pos, this.plant.getDefaultState)
  }
}

object StakedPlantSeed {
  val Properties: FabricItemSettings = (new FabricItemSettings)
    .group(ItemGroup.MISC)
}

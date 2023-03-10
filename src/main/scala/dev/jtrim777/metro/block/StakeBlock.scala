package dev.jtrim777.metro.block

import dev.jtrim777.metro.block.StakeBlock.Waterlogged
import dev.jtrim777.needle.block.BaseBlock
import net.minecraft.block.{Block, BlockState, Material, ShapeContext, Waterloggable, Blocks => VanillaBlocks}
import net.minecraft.block.AbstractBlock.Settings
import net.minecraft.fluid.{FluidState, Fluids}
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.state.StateManager
import net.minecraft.state.property.{BooleanProperty, Properties}
import net.minecraft.util.math.random.Random
import net.minecraft.util.math.{BlockPos, Direction}
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.{BlockView, WorldAccess, WorldView}

import java.lang.{Boolean => JBool}

class StakeBlock extends BaseBlock[StakeBlock](StakeBlock.Props) with Waterloggable {
  this.setDefaultState(this.stateManager.getDefaultState.`with`[JBool, JBool](StakeBlock.Waterlogged, false))

  override def getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape =
    StakeBlock.BlockShape

  override def getFluidState(state: BlockState): FluidState = {
    if (state.get(StakeBlock.Waterlogged)) {
      Fluids.WATER.getStill(false)
    } else super.getFluidState(state)
  }

  override def getPlacementState(ctx: ItemPlacementContext): BlockState = {
    this.getDefaultState
      .`with`[JBool, JBool](StakeBlock.Waterlogged, ctx.getWorld.getFluidState(ctx.getBlockPos).getFluid == Fluids.WATER)
  }

  override def getStateForNeighborUpdate(state: BlockState, direction: Direction, neighborState: BlockState,
                                         world: WorldAccess, pos: BlockPos, neighborPos: BlockPos): BlockState = {
    if (!this.canPlaceAt(state, world, pos)) {
      world.createAndScheduleBlockTick(pos, this, 1)
    }

    if (state.get(StakeBlock.Waterlogged)) {
      world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world))
    }

    super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
  }

  override def canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean = {
    val down = world.getBlockState(pos.down())

    down.isOf(VanillaBlocks.FARMLAND) ||
      down.isOf(this) ||
      down.getBlock.isInstanceOf[StakedPlant]
  }

  override def scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random): Unit = {
    if (!this.canPlaceAt(state, world, pos)) {
      world.breakBlock(pos, true)
    }

    super.scheduledTick(state, world, pos, random)
  }

  override def appendProperties(builder: StateManager.Builder[Block, BlockState]): Unit = {
    builder.add(Waterlogged)
  }
}

object StakeBlock {
  val BlockShape: VoxelShape = Block.createCuboidShape(7, -1, 7, 9, 15, 9)

  val Waterlogged: BooleanProperty = Properties.WATERLOGGED

  val Props: Settings = Settings.of(Material.WOOD)
    .sounds(BlockSoundGroup.WOOD)
    .strength(2.0F, 3.0F)
}

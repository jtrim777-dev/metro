package dev.jtrim777.metro.block

import dev.jtrim777.metro.MetroMod
import dev.jtrim777.needle.block.{BBlock, BaseBlock}
import dev.jtrim777.needle.util.syntax._
import net.minecraft.block.AbstractBlock.Settings
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.{Block, BlockState, FarmlandBlock, Fertilizable, Material, ShapeContext, Blocks => VanillaBlocks}
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.{Item, ItemStack, Items}
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.{BlockSoundGroup, SoundCategory, SoundEvents}
import net.minecraft.state.StateManager
import net.minecraft.state.property.{IntProperty, Properties}
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.{BlockPos, Direction}
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.{ActionResult, Hand, Identifier}
import net.minecraft.world.{BlockView, World, WorldAccess}

abstract class StakedPlant(val maxHeight: Int, val seed: Identifier, doTick: Boolean)
  extends BaseBlock[StakedPlant](StakedPlant.blockSettings(doTick)) with Fertilizable {
  this.setDefaultState(stateManager.getDefaultState.`with`[Integer, Integer](StakedPlant.Age, 0))

  override def getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape =
    StakedPlant.AgeToShape(age(state))

  override def getCollisionShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape =
    StakeBlock.BlockShape

  override def isTranslucent(state: BlockState, world: BlockView, pos: BlockPos): Boolean = true

  override def onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity,
                     hand: Hand, hit: BlockHitResult): ActionResult = {
    if (player.getStackInHand(hand).isOf(Items.BONE_MEAL)) {
      ActionResult.PASS
    } else {
      if (this.harvest(inst(state, pos), world, player.isSneaking)) {
        world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS,
          1.0F, 0.8F + world.random.nextFloat * 0.4F)
        ActionResult.SUCCESS
      } else super.onUse(state, world, pos, player, hand, hit)
    }
  }

  override def isFertilizable(world: BlockView, pos: BlockPos, state: BlockState, isClient: Boolean): Boolean = {
    val root = getRoot(inst(state, pos), world)
    root.block.canGrow(root, world)
  }

  override def canGrow(world: World, random: Random, pos: BlockPos, state: BlockState): Boolean = true

  override def grow(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState): Unit = {
    val root = getRoot(inst(state, pos), world)
    root.block.doGrow(root, world)
  }

  def harvest(self: Inst, world: World, propagate: Boolean): Boolean = {
    if (self.age >= StakedPlant.HarvestAge) {
      if (dropProduce(self, world)) {
        self.overwrite(self.stateWith[java.lang.Integer, java.lang.Integer](StakedPlant.Age, StakedPlant.GrowthAge), world)

        if (propagate) {
          getAbove(self, world) foreach { above =>
            this.harvest(above, world, propagate)
          }
        }

        true
      } else false
    } else false
  }

  def dropProduce(self: Inst, world: World): Boolean = {
    this.getProduce.exists { case (item, minDrop, maxDrop) =>
      val count = world.random.nextBetween(minDrop, maxDrop)
      val drop = new ItemStack(item, count)

      Block.dropStack(world, self.pos, drop)
      true
    }
  }

  def getProduce: Option[(Item, Int, Int)]

  def getRoot(self: Inst, world: BlockView): BBlock[StakedPlant.Root]
  def getLeaf(self: Inst, world: BlockView): Inst = getAbove(self, world)
    .map(a => a.block.getLeaf(a, world))
    .getOrElse(self)

  def getAbove(self: Inst, world: BlockView): Option[Inst] = {
    self.selfUp(world)
  }

  def getBelow(self: Inst, world: BlockView): Option[Inst]

  def height(self: Inst, world: BlockView): Int = 1 + getAbove(self, world)
    .map(a => a.block.height(a, world))
    .getOrElse(0)
  def age(state: BlockState): Int = state.get(StakedPlant.Age)

  def breakFull(self: Inst, world: World): Unit = {
    world.breakBlock(self.pos, true)
    Block.dropStack(world, self.pos, new ItemStack(Blocks.Stake, 1))
  }

  override def onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity): Unit = {
    super.onBreak(world, pos, state, player)
  }


  override def afterBreak(world: World, player: PlayerEntity, pos: BlockPos, state: BlockState,
                          blockEntity: BlockEntity, stack: ItemStack): Unit = {
    super.afterBreak(world, player, pos, state, blockEntity, stack)
    this.breakVine(inst(state, pos), world)
  }

  def breakVine(self: Inst, world: World, reBreak: Boolean = false): Unit = {
    if (reBreak) {
      world.breakBlock(self.pos, true)
    }

    self.overwrite(Blocks.Stake.getDefaultState, world)
  }

  def breakAll(self: Inst, world: World): Unit = {
    this.breakFull(self, world)
    this.getAbove(self, world).foreach { a => a.block.breakAll(a, world) }
  }

  def isPositionValid(pos: BlockPos, world: BlockView): Boolean

  override def getStateForNeighborUpdate(state: BlockState, direction: Direction, neighborState: BlockState,
                                         world: WorldAccess, pos: BlockPos, neighborPos: BlockPos): BlockState = {
    if (!this.isPositionValid(pos, world)) {
      world.createAndScheduleBlockTick(pos, this, 1)
    }

    super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
  }

  override def appendProperties(builder: StateManager.Builder[Block, BlockState]): Unit = {
    builder.add(StakedPlant.Age)
  }
}

object StakedPlant {
  abstract class Root(maxHeight: Int, seed: Identifier, private val child: Ext)
    extends StakedPlant(maxHeight, seed, true) {
    child.rootBlock = this

    override def getRoot(self: Inst, world: BlockView): BBlock[StakedPlant.Root] = BBlock[Root](self.state, self.pos)

    def getBelow(self: Inst, world: BlockView): Option[Inst] = None

    def canGrow(self: Inst, world: BlockView): Boolean = {
      val vine = StakedPlant.collectAll(self, world)

      vine.exists(_.age < StakedPlant.HarvestAge) || {
        val leaf = this.getLeaf(self, world)
        val up = leaf.up(world)

        up.block.isInstanceOf[StakeBlock] && !up.get(StakeBlock.Waterlogged) && vine.length < this.maxHeight
      }
    }

    def doGrow(self: Inst, world: World): Boolean = {
      if (world.random.nextFloat() < AgeVsExtendChance) {
        this.doAge(self, world)
      } else this.doExtend(self, world)
    }

    private def doAge(self: Inst, world: World): Boolean = {
      val vine = StakedPlant.collectAll(self, world)

      val ageable = vine.find(_.age < StakedPlant.HarvestAge)

      ageable.exists { selected =>
        selected.overwrite(selected.stateWith[Integer, Integer](Age, selected.age + 1), world)
        true
      }
    }

    private def doExtend(self: Inst, world: World): Boolean = {
      val leaf = this.getLeaf(self, world)
      val up = leaf.up(world)

      if (up.block.isInstanceOf[StakeBlock] && !up.get(StakeBlock.Waterlogged)) {
        up.overwrite(child.getDefaultState.`with`[Integer, Integer](Age, 1), world)
        true
      } else false
    }

    def isPositionValid(pos: BlockPos, world: BlockView): Boolean = {
      world.getBlockState(pos.down()).isOf(VanillaBlocks.FARMLAND)
    }

    override def scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random): Unit = {
      if (!this.isPositionValid(pos, world)) {
        this.breakAll(inst(state, pos), world)
      }
    }

    private def collectMoisture(pos: BlockPos, world: BlockView): Float = {
      pos.range(1, 0, 1).foldLeft(0f) { (acc, bp) =>
        val sx = world.getBlockState(bp)

        val m = if (sx.isOf(VanillaBlocks.FARMLAND)) {
          sx.get(FarmlandBlock.MOISTURE).toFloat / FarmlandBlock.MAX_MOISTURE.toFloat
        } else 0f

        val rm = if (bp == pos) m * 2 else m

        acc + rm
      } / 70f
    }

    override def randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random): Unit = {
      val lightLevel = world.getBaseLightLevel(pos, 0)
      if (lightLevel > 9) {
        val lightChance = ((lightLevel - 9).toFloat / 7f) * (1f/3f)
        val mChance = collectMoisture(pos, world) * (1f/3f)
        val mult = 1f - (lightChance + mChance)

        val chance = BaseGrowthChance * mult// * (1f - lightChance) * (1f - mChance)

        if (random.nextInt(chance.toInt) == 0) {
          this.doGrow(inst(state, pos), world)
        }
      }
    }
  }

  abstract class Ext(maxHeight: Int, seed: Identifier) extends StakedPlant(maxHeight, seed, false) {
    private[StakedPlant] var rootBlock: Root = null

    override def getRoot(self: Inst, world: BlockView): BBlock[Root] = getBelow(self, world) match {
      case Some(below) => below.block.getRoot(below, world)
      case None => null // TODO: Can this possibly be fixed?
    }

    private def makeRoot(self: Inst, world: World): BBlock[Root] = {
      val next = BBlock[Root](rootBlock.getDefaultState.`with`[Integer, Integer](Age, self.age), self.pos)

      self.overwrite(next.state, world)
      next
    }

    override def getBelow(self: Inst, world: BlockView): Option[Inst] = {
      self.selfDown(world)
    }

    override def isPositionValid(pos: BlockPos, world: BlockView): Boolean = {
      val down = world.getBlockState(pos.down())

      down.isOf(this) || down.isOf(rootBlock)
    }

    override def scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random): Unit = {
      if (!this.isPositionValid(pos, world)) {
        val self = inst(state, pos)

        if (rootBlock.isPositionValid(pos, world)) {
          this.makeRoot(self, world)
        } else {
          this.breakAll(self, world)
        }
      }

      super.scheduledTick(state, world, pos, random)
    }
  }

  private def blockSettings(tick: Boolean): Settings = {
    val base = Settings.of(Material.PLANT)
      .sounds(BlockSoundGroup.CROP)
      .breakInstantly()

    if (tick) base.ticksRandomly() else base
  }

  private val SproutShape: VoxelShape = Block.createCuboidShape(2, 0, 2, 14, 16, 14)
  private val JuniorShape: VoxelShape = Block.createCuboidShape(2, 0, 2, 14, 16, 14)
  private val TeenShape: VoxelShape = Block.createCuboidShape(2, 0, 2, 14, 16, 14)
  private val FinalShape: VoxelShape = Block.createCuboidShape(2, 0, 2, 14, 16, 14)

  private val AgeToShape: Map[Int, VoxelShape] = Map(
    0 -> SproutShape,
    1 -> JuniorShape,
    2 -> TeenShape,
    3 -> FinalShape, 4 -> FinalShape, 5 -> FinalShape
  )

  private val Age: IntProperty = Properties.AGE_5
  /*
  0 - Sprout
  1 - 1/3 growth
  2 - 2/3 growth
  3 - Full stake height (can now extend)
  4 - Budding flowers
  5 - Harvestable
   */

  private val GrowthAge: Int = 3
  private val HarvestAge: Int = 5

  private val AgeVsExtendChance: Float = 2f/3f
  private val BaseGrowthChance: Float = 25f

  private def collectAll(bottom: StakedPlant#Inst, world: BlockView): List[StakedPlant#Inst] =
    bottom :: bottom.block.getAbove(bottom, world).map(collectAll(_, world)).getOrElse(List.empty)

  implicit class InstOps(inst: StakedPlant#Inst) {
    def age: Int = inst.state.get(Age)
  }

  def create(maxHeight: Int, seed: Identifier, produce: => Item,
             minProduce: Int, maxProduce: Int): (StakedPlant.Root, StakedPlant.Ext) = {
    val child = new Ext(maxHeight, seed) {
      override def getProduce: Option[(Item, Int, Int)] = Some((produce, minProduce, maxProduce))
    }

    val root = new Root(maxHeight, seed, child) {
      override def getProduce: Option[(Item, Int, Int)] = Some((produce, minProduce, maxProduce))
    }

    (root, child)
  }

  def create(maxHeight: Int, seed: Identifier): (StakedPlant.Root, StakedPlant.Ext) = {
    val child = new Ext(maxHeight, seed) {
      override def getProduce: Option[(Item, Int, Int)] = None
    }

    val root = new Root(maxHeight, seed, child) {
      override def getProduce: Option[(Item, Int, Int)] = None
    }

    (root, child)
  }
}

package dev.jtrim777.metro.item

import dev.jtrim777.needle.enrich.EnrichedItem
import dev.jtrim777.needle.inv.Inventory
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.{ItemGroup, ItemStack, ItemUsageContext}
import dev.jtrim777.needle.nbt.generic._
import net.minecraft.block.ChestBlock
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.{PlayerEntity, PlayerInventory}
import net.minecraft.text.Text
import net.minecraft.util.{ActionResult, Formatting, Hand, TypedActionResult}
import net.minecraft.world.World

import java.util
import net.minecraft.inventory.{Inventory => MInventory}

class CoinPouch extends EnrichedItem[CoinPouch.Data](new FabricItemSettings().group(ItemGroup.TOOLS).maxCount(1)) {
  override protected def initialData(stack: ItemStack): CoinPouch.Data = CoinPouch.Data(0,0,0)

  override def use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult[ItemStack] = {
    val pouch = user.getStackInHand(hand)
    if (user.isSneaking) {
      collectAll(pouch, user.getInventory)
      TypedActionResult.success(pouch)
    } else {
      if (load(pouch).storedValue == 0) {
        super.use(world, user, hand)
      } else {
        popOne(pouch, user.getInventory)
        TypedActionResult.success(pouch)
      }
    }
  }

  override def useOnBlock(context: ItemUsageContext): ActionResult = {
    if (context.getPlayer.isSneaking) {
      chestInvOfTarget(context) match {
        case Some(inv) =>
          collectAll(context.getStack, inv)
          ActionResult.SUCCESS
        case None => ActionResult.PASS
      }
    } else {
      if (load(context.getStack).storedValue > 0) {
        chestInvOfTarget(context) match {
          case Some(inv) =>
            if (giveAll(context.getStack, Inventory.from(inv))) ActionResult.SUCCESS else ActionResult.PASS
          case None => ActionResult.PASS
        }
      } else ActionResult.PASS
    }
  }

  override def appendTooltip(stack: ItemStack, world: World,
                             tooltip: util.List[Text], context: TooltipContext): Unit = {
    super.appendTooltip(stack, world, tooltip, context)

    val value = load(stack).storedValue

    val baseText = if (value == 0) {
      Text.translatable("item.metro.coin_pouch.empty_tooltip")
    } else if (value == 1) {
      Text.translatable("item.metro.coin.value_tooltip_single")
    } else Text.translatable("item.metro.coin.value_tooltip", value)

    val colored = baseText.formatted(Formatting.GRAY)

    tooltip.add(colored)
  }

  private def chestInvOfTarget(ctx: ItemUsageContext): Option[MInventory] = {
    val bs = ctx.getWorld.getBlockState(ctx.getBlockPos)
    val block = bs.getBlock
    block match {
      case chestBlock: ChestBlock =>
        Option(ChestBlock.getInventory(chestBlock, bs, ctx.getWorld, ctx.getBlockPos, true))
      case _ => None
    }
  }

  private def collectAll(pouch: ItemStack, inv: MInventory): Unit = {
    val (copper: Int, iron: Int, gold: Int) = Inventory.from(inv).foldLeft((0,0,0)) { case ((c,i,g), is) =>
      if (is.getItem.getClass == classOf[Coin]) {
        val coin = is.getItem.asInstanceOf[Coin]
        val amt = is.getCount
        is.setCount(0)

        coin.cubitValue match {
          case 1 => (c+amt,i,g)
          case 16 => (c,i+amt,g)
          case 64 => (c,i,g+amt)
          case x => throw new IllegalStateException(s"A magic coin appeared with value $x")
        }
      } else (c,i,g)
    }

    val data = load(pouch)
    update(pouch, data.add(copper, iron, gold))
  }

  private def popOne(pouch: ItemStack, inv: PlayerInventory): Unit = {
    val data = load(pouch)

    data.asItems.headOption.foreach { is =>
      val justOne = new ItemStack(is.getItem)
      inv.offerOrDrop(justOne.copy())
      val newData = data.remove(justOne)
      update(pouch, newData)
    }
  }

  private def giveAll(pouch: ItemStack, inv: Inventory): Boolean = {
    val data = load(pouch)
    val items = data.asItems
    val remainder = items.map(inv.insert)
    val newData = CoinPouch.Data.fromItems(remainder)

    update(pouch, newData)

    newData.storedValue < data.storedValue
  }
}

object CoinPouch {
  def attemptLoad(is: ItemStack): Option[Data] = EnrichedItem.attemptLoad[Data](is)

  case class Data(copper: Int, iron: Int, gold: Int) {
    def storedValue: Int = (copper * Coin.CopperValue) + (iron * Coin.IronValue) + (gold * Coin.GoldValue)

    def add(c: Int, i: Int, g: Int): Data = this.copy(copper = copper + c, iron = iron + i, gold = gold + g)

    def asItems: List[ItemStack] = {
      val golds = if (gold > 0) Some(new ItemStack(Items.lookup("gold_coin"))) else None
      val irons = if (iron > 0) Some(new ItemStack(Items.lookup("iron_coin"))) else None
      val coppers = if (copper > 0) Some(new ItemStack(Items.lookup("copper_coin"))) else None

      List(golds, irons, coppers).flatten
    }

    def remove(is: ItemStack): Data = {
      is.getItem match {
        case c:Coin => c.cubitValue match {
          case 1 => this.copy(copper = math.max(copper - is.getCount, 0))
          case 16 => this.copy(iron = math.max(iron - is.getCount, 0))
          case 64 => this.copy(gold = math.max(gold - is.getCount, 0))
        }
        case _ => this
      }
    }
  }
  object Data {
    def fromItems(its: List[ItemStack]): Data = {
      its.foldLeft(Data(0,0,0)) { (data, is) =>
        is.getItem match {
          case c: Coin => c.cubitValue match {
            case 1 => data.copy(copper = data.copper + is.getCount)
            case 16 => data.copy(iron = data.iron + is.getCount)
            case 64 => data.copy(gold = data.gold + is.getCount)
          }
          case _ => data
        }
      }
    }
  }
}

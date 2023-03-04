package dev.jtrim777.metro.entity

import dev.jtrim777.needle.inv.Inventory
import net.minecraft.entity.attribute.{DefaultAttributeContainer, EntityAttributes}
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.{EntityType, Npc}
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.World

class Metropolite(typ: EntityType[Metropolite], world: World) extends PassiveEntity(typ, world) with Npc {
  val inventory: Inventory = ???



  override def createChild(world: ServerWorld, entity: PassiveEntity): PassiveEntity = ???
}

object Metropolite {
  def apply(typ: EntityType[Metropolite], world: World): Metropolite = new Metropolite(typ, world)

  def attributes: DefaultAttributeContainer.Builder = MobEntity.createMobAttributes
    .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5)
    .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0)
}

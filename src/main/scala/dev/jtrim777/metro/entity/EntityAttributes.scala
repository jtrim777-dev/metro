package dev.jtrim777.metro.entity

import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.minecraft.entity.{EntityType, LivingEntity}
import net.minecraft.entity.attribute.DefaultAttributeContainer

object EntityAttributes {
  def register(): Unit = {
//    registerOne(Entities.MetropoliteType, Metropolite.attributes)
  }

  private def registerOne(entityType: EntityType[_ <: LivingEntity], attrs: DefaultAttributeContainer.Builder): Unit = {
    FabricDefaultAttributeRegistry.register(entityType, attrs)
  }
}

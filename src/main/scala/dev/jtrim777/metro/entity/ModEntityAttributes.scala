package dev.jtrim777.metro.entity

import dev.jtrim777.metro.MetroMod
import dev.jtrim777.needle.registry.{DelayedRegistry, registry}
import net.minecraft.entity.attribute.{ClampedEntityAttribute, EntityAttribute}
import net.minecraft.util.registry.Registry

@registry
object ModEntityAttributes extends DelayedRegistry[EntityAttribute] {
  override def registry: Registry[EntityAttribute] = Registry.ATTRIBUTE
  override def namespace: String = MetroMod.ModID

  lazy val StepHeightBonus: EntityAttribute = this.lookup("step_height_bonus")

  def buildStepHeightBonus(): EntityAttribute = {
    new ClampedEntityAttribute(s"generic.$namespace.step-height-bonus", 0f, -1024f, 1024f) with SyncedAttribute {}
  }


}

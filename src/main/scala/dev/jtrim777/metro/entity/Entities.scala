package dev.jtrim777.metro.entity

import dev.jtrim777.metro.MetroMod
import dev.jtrim777.needle.registry.{DelayedRegistry, registered}
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.entity.{EntityDimensions, EntityType, SpawnGroup}
import net.minecraft.util.registry.Registry

object Entities extends DelayedRegistry[EntityType[_]] {
  override def registry: Registry[EntityType[_]] = Registry.ENTITY_TYPE
  override def namespace: String = MetroMod.ModID

//  @registered lazy val MetropoliteType: EntityType[Metropolite] = FabricEntityTypeBuilder
//    .create(SpawnGroup.MISC, Metropolite.apply)
//    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
//    .trackRangeChunks(10)
//    .build()
}

package dev.jtrim777.metro.world

import dev.jtrim777.needle.struct.StructurePoolRegistry
import dev.jtrim777.needle.util.syntax._
import org.apache.logging.log4j.{LogManager, Logger}
import dev.jtrim777.metro.ctx
import net.minecraft.structure.pool.StructurePool.Projection

object Structures {
  private val Log: Logger = LogManager.getLogger(s"metro:structures")

  def init(): Unit = {
    Log.info("Registering mod structures")
    this.registerPoolOverrides()
  }

  private def registerPoolOverrides(): Unit = {
    StructurePoolRegistry.registerSimple(vanilla"village/plains/houses")(
      id = id"village/plains/plains_vineyard",
      weight = 3,
      projection = Projection.TERRAIN_MATCHING
    )
  }
}

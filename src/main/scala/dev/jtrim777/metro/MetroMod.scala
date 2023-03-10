package dev.jtrim777.metro

import dev.jtrim777.metro.block.Blocks
import dev.jtrim777.metro.item.Items
import dev.jtrim777.metro.world.Structures
import dev.jtrim777.needle.struct.OnAddStructurePool
import net.fabricmc.api.ModInitializer
import org.apache.logging.log4j.{LogManager, Logger}

object MetroMod extends ModInitializer {
    val ModID: String = "metro"
    val Log: Logger = LogManager.getLogger(ModID)

    override def onInitialize(): Unit = {
        Log.info("Begin mod init")
        Blocks.register()
        Items.register()

        Structures.init()
    }
}

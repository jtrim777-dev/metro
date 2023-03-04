package dev.jtrim777.metro

import dev.jtrim777.metro.item.ItemProperties
import net.fabricmc.api.ClientModInitializer

object MetroModClient extends ClientModInitializer {
    override def onInitializeClient(): Unit = {
        MetroMod.Log.info("Client initialized")
        ItemProperties.register()
    }
}

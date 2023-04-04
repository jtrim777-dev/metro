package dev.jtrim777.metro

import dev.jtrim777.metro.block.Blocks
import dev.jtrim777.metro.entity.PlayerAttributesUtil
import dev.jtrim777.metro.item.ItemProperties
import net.fabricmc.api.ClientModInitializer

object MetroModClient extends ClientModInitializer {
    override def onInitializeClient(): Unit = {
        MetroMod.Log.info("Client initialized")
        ItemProperties.register()
        Blocks.onClientInit()
        PlayerAttributesUtil.registerHandler()
    }
}

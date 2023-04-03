package dev.jtrim777.metro.magic

import dev.jtrim777.metro.magic.enchants.{ArmorEnchant, Enchants}

object MagicModule {
  def commonInit(): Unit = {
    ArmorEnchant.registerEventHandler()
    Enchants.register()
  }
}

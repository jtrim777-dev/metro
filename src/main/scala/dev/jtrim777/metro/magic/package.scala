package dev.jtrim777.metro

import net.minecraft.item.ItemStack
import dev.jtrim777.needle.nbt._

package object magic {
  implicit class ISOps(val target: ItemStack) {
    def getInlay: Option[Inlay] = {
      val nx = target.getOrCreateNbt()

      if (nx.contains("inlay")) {
        Some(nx.get("inlay").as[Inlay])
      } else None
    }
  }
}

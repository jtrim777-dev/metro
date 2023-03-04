package dev.jtrim777

import net.minecraft.util.Identifier

package object metro {
  def id(name: String): Identifier = new Identifier(MetroMod.ModID, name)
}

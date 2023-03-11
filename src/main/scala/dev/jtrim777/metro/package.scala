package dev.jtrim777

import dev.jtrim777.needle.util.ModContext

package object metro {
  implicit val ctx: ModContext = ModContext.Simple(MetroMod.ModID)
}

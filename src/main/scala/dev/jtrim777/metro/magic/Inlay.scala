package dev.jtrim777.metro.magic

import dev.jtrim777.metro.magic.Inlay.EffectInst
import dev.jtrim777.needle.nbt.{NBTCodec, IdentCodec}
import dev.jtrim777.needle.nbt.generic._
import dev.jtrim777.needle.nbt.derivation._
import net.minecraft.enchantment.Enchantment
import net.minecraft.util.registry.Registry

case class Inlay(name: Option[String], effects: Seq[EffectInst])

object Inlay {
  case class EffectInst(enchant: Enchantment, level: Int)

  implicit val ECodec: NBTCodec[Enchantment] = NBTCodec.from[Enchantment](
    IdentCodec.contramap(enchant => Registry.ENCHANTMENT.getId(enchant)),
    IdentCodec.map(id => Registry.ENCHANTMENT.get(id))
  )

  implicit val EICodec: NBTCodec[EffectInst] = deriveCoder[EffectInst]
  implicit val InlayCodec: NBTCodec[Inlay] = deriveCoder[Inlay]
}

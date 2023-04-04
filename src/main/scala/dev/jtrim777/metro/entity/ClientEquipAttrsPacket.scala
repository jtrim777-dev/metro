package dev.jtrim777.metro.entity

import dev.jtrim777.needle.util.syntax.IdHelper
import dev.jtrim777.needle.nbt._
import dev.jtrim777.needle.nbt.generic._
import dev.jtrim777.metro.ctx
import net.fabricmc.fabric.api.networking.v1.{PacketByteBufs, ServerPlayNetworking}
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

import java.util.UUID

case class ClientEquipAttrsPacket(remove: Map[Identifier, Seq[UUID]],
                                  add: Map[Identifier, Seq[EntityAttributeModifier]]) {
  def toPacket: PacketByteBuf = {
    val packet = PacketByteBufs.create()
    val nbt = ClientEquipAttrsPacket.PacketCodec.encode(this).downCast(NbtCompound.TYPE)
    packet.writeNbt(nbt)

    packet
  }

  def send(target: ServerPlayerEntity): Unit = {
    ServerPlayNetworking.send(target, ClientEquipAttrsPacket.ID, this.toPacket)
  }
}

object ClientEquipAttrsPacket {
  val ID: Identifier = id"client_equipment_attributes_sync"

  implicit val EAMCodec: NBTCodec[EntityAttributeModifier] = codec[EntityAttributeModifier](e => e.toNbt,
    n => EntityAttributeModifier.fromNbt(n.downCast(NbtCompound.TYPE)))
  implicit val UUIDCodec: NBTCodec[UUID] = NBTCodec.from(StringCodec.contramap(_.toString),
    StringCodec.map(s => UUID.fromString(s)))

  val PacketCodec: NBTCodec[ClientEquipAttrsPacket] = NBTCodec.from[ClientEquipAttrsPacket](
    implicitly[NBTEncoder[ClientEquipAttrsPacket]],
    implicitly[NBTDecoder[ClientEquipAttrsPacket]]
  )


  def parse(raw: PacketByteBuf): ClientEquipAttrsPacket = {
    val nbt = raw.readNbt()

    PacketCodec.decode(nbt)
  }
}

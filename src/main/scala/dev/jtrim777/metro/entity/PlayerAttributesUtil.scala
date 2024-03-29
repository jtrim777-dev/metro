package dev.jtrim777.metro.entity

import com.google.common.collect.{ArrayListMultimap, Multimap, Multimaps}
import dev.jtrim777.metro.MetroMod
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.{EntityAttribute, EntityAttributeModifier}
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

import scala.jdk.CollectionConverters._

object PlayerAttributesUtil {
  def syncUpdates(remove: Multimap[EntityAttribute, EntityAttributeModifier],
                  add: Multimap[EntityAttribute, EntityAttributeModifier],
                  self: ServerPlayerEntity): Unit = {
    val oremove = Option(remove)
    val oadd = Option(add)

    val nremove = oremove.map(r => convertAttrs(r, _.getId)).getOrElse(Map.empty)
    val nadd = oadd.map(a => convertAttrs(a, a => a)).getOrElse(Map.empty)

    if (nremove.nonEmpty || nadd.nonEmpty) {
      val pkt = ClientEquipAttrsPacket(nremove, nadd)
      pkt.send(self)
    }
  }

  private def receiveUpdates(player: ClientPlayerEntity, packet: ClientEquipAttrsPacket): Unit = {
    val attrs = player.getAttributes

//    MetroMod.Log.info(s"Player $player told to add attrs ${packet.add} and remove ${packet.remove}")

    packet.remove.foreachEntry { (id, mods) =>
      val tgt = Registry.ATTRIBUTE.get(id)
      val attr = attrs.getCustomInstance(tgt)
      mods.foreach(attr.removeModifier)
    }

    packet.add.foreachEntry { (id, mods) =>
      val tgt = Registry.ATTRIBUTE.get(id)
      val attr = attrs.getCustomInstance(tgt)
      mods.foreach(attr.addTemporaryModifier)
    }
  }

  def registerHandler(): Unit = {
      ClientPlayNetworking.registerGlobalReceiver(ClientEquipAttrsPacket.ID, {(client, _, packet, _) =>
        val msg = ClientEquipAttrsPacket.parse(packet)
        client.execute { () =>
          val player = client.player
          receiveUpdates(player, msg)
        }
      })
  }

  def registerServerHandler(): Unit = {
    ServerPlayConnectionEvents.JOIN.register { (handler, _, _) =>
      val player = handler.player
      val equippedAttrs = EquipmentSlot.values()
        .map(slot => player.getEquippedStack(slot).getAttributeModifiers(slot))
        .filter(s => !s.isEmpty)
        .foldLeft(ArrayListMultimap.create[EntityAttribute, EntityAttributeModifier]()) { (l, r) =>
          l.putAll(r)
          l
        }

      syncUpdates(null, equippedAttrs, player)
    }
  }

  private def convertAttrs[T](raw: Multimap[EntityAttribute, EntityAttributeModifier], f: EntityAttributeModifier => T): Map[Identifier, List[T]] = {
    Multimaps.filterKeys(raw, (k: EntityAttribute) => k.isInstanceOf[SyncedAttribute])
      .asMap()
      .asScala
      .map { case (attribute, modifiers) =>
        Registry.ATTRIBUTE.getId(attribute) -> modifiers.asScala.map(f).toList
      }
      .toMap
  }
}

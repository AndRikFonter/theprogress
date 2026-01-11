package com.example.theprogress.mana.player.mana

import com.example.theprogress.TheProgress.Companion.MODID
import com.example.theprogress.mana.Attachments
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier.fromNamespaceAndPath
import net.neoforged.neoforge.network.handling.IPayloadContext

data class SyncPayload(val playerMana: PlayerMana) : CustomPacketPayload {
    companion object {
        val ID = CustomPacketPayload.Type<SyncPayload>(fromNamespaceAndPath(MODID, "player_mana_sync"))
        val CODEC = PlayerMana.STREAM_CODEC.map(::SyncPayload, SyncPayload::playerMana)

        fun handle(payload: SyncPayload, context: IPayloadContext) {
            context.enqueueWork {
                // Обновляем данные на клиенте
                context.player().setData(Attachments.PLAYERMANA, payload.playerMana)
            }
        }
    }

    override fun type() = ID
}
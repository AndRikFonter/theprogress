package com.example.theprogress.mana.player.reborn

import com.example.theprogress.TheProgress.Companion.MODID
import com.example.theprogress.mana.Attachments
import com.example.theprogress.mana.player.meditation.Meditation
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier.fromNamespaceAndPath
import net.neoforged.neoforge.network.handling.IPayloadContext

data class SyncPayload(val reborn: Reborn) : CustomPacketPayload {
    companion object {
        val ID = CustomPacketPayload.Type<SyncPayload>(fromNamespaceAndPath(MODID, "reborn_sync"))
        val CODEC = Reborn.STREAM_CODEC.map(::SyncPayload, SyncPayload::reborn)

        fun handle(payload: SyncPayload, context: IPayloadContext) {
            context.enqueueWork {
                // Обновляем данные на клиенте
                context.player().setData(Attachments.REBORN, payload.reborn)
            }
        }
    }

    override fun type() = ID
}

package com.example.theprogress.mana.player.meditation

import com.example.theprogress.TheProgress.Companion.MODID
import com.example.theprogress.mana.Attachments
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier.fromNamespaceAndPath
import net.neoforged.neoforge.network.handling.IPayloadContext

data class SyncPayload(val meditation: Meditation) : CustomPacketPayload {
    companion object {
        val ID = CustomPacketPayload.Type<SyncPayload>(fromNamespaceAndPath(MODID, "meditation_sync"))
        val CODEC = Meditation.STREAM_CODEC.map(::SyncPayload, SyncPayload::meditation)

        fun handle(payload: SyncPayload, context: IPayloadContext) {
            context.enqueueWork {
                // Обновляем данные на клиенте
                context.player().setData(Attachments.MEDITATION, payload.meditation)
            }
        }
    }

    override fun type() = ID
}

package com.example.theprogress.mana.player.magichealth

import com.example.theprogress.TheProgress.Companion.MODID
import com.example.theprogress.mana.Attachments
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier.fromNamespaceAndPath
import net.neoforged.neoforge.network.handling.IPayloadContext

data class SyncPayload(val magicHealth: MagicHealth) : CustomPacketPayload {
    companion object {
        val ID = CustomPacketPayload.Type<SyncPayload>(fromNamespaceAndPath(MODID, "magic_health_sync"))
        val CODEC = MagicHealth.STREAM_CODEC.map(::SyncPayload, SyncPayload::magicHealth)

        fun handle(payload: SyncPayload, context: IPayloadContext) {
            context.enqueueWork {
                // Обновляем данные на клиенте
                context.player().setData(Attachments.MAGICHEALTH, payload.magicHealth)
            }
        }
    }

    override fun type() = ID
}
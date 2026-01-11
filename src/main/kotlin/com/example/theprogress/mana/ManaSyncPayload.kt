package com.example.theprogress.mana

import com.example.theprogress.TheProgress.Companion.MODID
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier.fromNamespaceAndPath
import net.neoforged.neoforge.network.handling.IPayloadContext

data class ManaSyncPayload(val mana: Mana) : CustomPacketPayload {
    companion object {
        val ID = CustomPacketPayload.Type<ManaSyncPayload>(fromNamespaceAndPath(MODID, "mana_sync"))
        val CODEC = Mana.STREAM_CODEC.map(::ManaSyncPayload, ManaSyncPayload::mana)

        fun handle(payload: ManaSyncPayload, context: IPayloadContext) {
            context.enqueueWork {
                // Обновляем данные на клиенте
                context.player().setData(Attachments.MANA, payload.mana)
            }
        }
    }

    override fun type() = ID
}
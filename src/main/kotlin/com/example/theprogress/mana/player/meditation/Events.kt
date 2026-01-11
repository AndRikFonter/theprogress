package com.example.theprogress.mana.player.meditation

import com.example.theprogress.TheProgress.Companion.MODID
import com.example.theprogress.mana.Attachments
import com.example.theprogress.mana.player.mana.SyncPayload
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import net.neoforged.neoforge.network.PacketDistributor

@EventBusSubscriber(modid = MODID)
object Events {
    @JvmStatic
    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent.Post) {
        val player = event.entity
        if (!player.level().isClientSide && player.tickCount % 200 == 0) {
            val meditation = player.getData(Attachments.MEDITATION)
            meditation.playerTick(player as ServerPlayer)
            // Отправляем ману игроку
            PacketDistributor.sendToPlayer(player, SyncPayload(meditation))
        }
    }
}

package com.example.theprogress.mana

import com.example.theprogress.TheProgress.Companion.MODID
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import net.neoforged.neoforge.network.PacketDistributor

//@EventBusSubscriber(modid = MODID)
//object ManaEvents {
//
//    @JvmStatic
//    @SubscribeEvent
//    fun onPlayerTick(event: PlayerTickEvent.Post) {
//        val player = event.entity
//        if (!player.level().isClientSide && player.tickCount % 20 == 0) {
//            val mana = player.getData(Attachments.MANA)
//            // Отправляем ману игроку
//            PacketDistributor.sendToPlayer(player as net.minecraft.server.level.ServerPlayer, ManaSyncPayload(mana))
//        }
//    }
//}

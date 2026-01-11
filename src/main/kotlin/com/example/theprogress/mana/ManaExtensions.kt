package com.example.theprogress.mana

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor


//fun Player.modifyMana(action: (Mana) -> Unit) {
//    val mana = this.getData(Attachments.MANA)
//    action(mana) // Выполняем consume, fill и т.д.
//
//    // 1. Помечаем данные как измененные (обязательно для сохранения)
//    this.setData(Attachments.MANA, mana)
//
//    // 2. Синхронизируем с клиентом
//    if (this is ServerPlayer) {
//        PacketDistributor.sendToPlayer(this, ManaSyncPayload(mana))
//    }
//}

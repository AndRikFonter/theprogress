package com.example.theprogress.mana.player.mana

import com.example.theprogress.mana.IMana
import com.example.theprogress.mana.IManaTaker
import net.minecraft.server.level.ServerPlayer

interface IPlayerMana : IMana, IManaTaker {
    //val manaPerTick: Float
//    fun playerTick(playerIn: ServerPlayer)
    fun artificialConsume(points: Int, player: ServerPlayer): Boolean
//    fun sendToPlayer(player: ServerPlayer)
}

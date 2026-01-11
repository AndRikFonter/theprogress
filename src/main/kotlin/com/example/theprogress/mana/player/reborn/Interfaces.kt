package com.example.theprogress.mana.player.reborn

import net.minecraft.server.level.ServerPlayer

interface IRebornBase {
    val rebornPrepare: Float
    val isInReborn: Boolean
    var rebornProgress: Float
    val rebornStage: Int
}

interface IReborn : IRebornBase {
//    fun sendToPlayer(player: ServerPlayer)
    fun playerTick(playerIn: ServerPlayer)
    //returned last needed byte.
    fun copy(playerProperties: IReborn)
}

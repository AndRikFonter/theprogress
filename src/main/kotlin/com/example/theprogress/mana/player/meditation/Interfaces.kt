package com.example.theprogress.mana.player.meditation

import net.minecraft.server.level.ServerPlayer

interface IBaseMeditation {
    val inMeditation: Boolean
    val meditationProgress: Float
    var sinkingSpeed: Float
}

interface IMeditation : IBaseMeditation {
    fun playerTick(playerIn: ServerPlayer)
    fun startMeditation(playerIn: ServerPlayer)
    fun stopMeditation(playerIn: ServerPlayer)
//    fun sendToPlayer(player: ServerPlayer)
    //returned last needed byte.
    fun copy(playerProperties: IMeditation)
}

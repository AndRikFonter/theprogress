package com.example.theprogress.mana.player.magichealth

import net.minecraft.server.level.ServerPlayer

interface IMagicHealthBase {
    val curMagicHealth: Int
    val maxMagicHealth: Int
}

interface IMagicHealth : IMagicHealthBase {
    fun playerTick(playerIn: ServerPlayer)
//    fun toByteArray(): ByteArray
    fun harmMagicHealth(points: Int)
    fun healMagicHealth(points: Int)
//    fun sendToPlayer(player: ServerPlayer)
    //returned last needed byte.
//    fun loadFromByteArray(buff: ByteArray): Int
    fun copy(playerProperties: IMagicHealth)
}

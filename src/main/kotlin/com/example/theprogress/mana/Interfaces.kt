package com.example.theprogress.mana

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level

interface IManaBase {
    val currentMana: Int
    val baseMaxMana: Int
}

interface IMana : IManaBase {
    fun consume(points: Int): Boolean
    //Returns how much mana was transferred
    fun give(points: Int, rate: Float): Int
    fun fill(points: Int)
    fun copy(mana: IMana)
//    fun onContentsChanged()
}

interface IManaSpreader : IManaBase {
    val maxSpread: Int
    fun spread(points: Int, rate: Float): Int
}

interface IManaReceiver : IManaBase {
    val maxTransfer: Int
    fun transfer(points: Int)
}

interface IManaTaker {
//    val isConnectedToManaSpreader: Boolean
//    val spreaderWorldPos: String
    fun connectToManaSpreader(manaSpreaderPos: BlockPos, levelManaSpreader: Level)
    fun disconnectToManaSpreader()
    fun getMana(points: Int, levelManaSpreader: Level, manaConsumer: BlockPos, sensitivity: Float): Int
    val rate: Float
}

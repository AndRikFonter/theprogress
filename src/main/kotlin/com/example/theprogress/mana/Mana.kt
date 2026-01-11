package com.example.theprogress.mana

import com.example.theprogress.mana.Attachments.MANA
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.PacketDistributor


open class Mana(
    override var currentMana: Int = 250,
    override var baseMaxMana: Int = 1000
) : IMana {

    // Initializers
    companion object {
        val CODEC: MapCodec<Mana> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.INT.fieldOf("currentMana").forGetter(Mana::currentMana),
                Codec.INT.fieldOf("baseMaxMana").forGetter(Mana::baseMaxMana)
            ).apply(instance, ::Mana)
        }

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, Mana> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, Mana::currentMana,
            ByteBufCodecs.VAR_INT, Mana::baseMaxMana,
            ::Mana
        )
    }

    override fun copy(mana: IMana) {
        this.currentMana = mana.currentMana
//        onContentsChanged()
    }

    // Important

    // Methods
    override fun consume(points: Int): Boolean {
        if (currentMana >= points) {
            currentMana -= points
//            onContentsChanged()
            return true
        }
        return false
    }

//    override fun onContentsChanged() {
//        PacketDistributor.sendToAllPlayers(this)
//    }

    override fun give(points: Int, rate: Float): Int {
        if (points <= 0){
            return 0
        }
        var afterRateToSend = (points * rate).toInt()
        if (afterRateToSend == 0) return 0

        val afterRateConsume = (afterRateToSend / rate).toInt()
        if (afterRateConsume == 0){
            return 0
        }
        if (currentMana >= afterRateConsume) {
            currentMana -= afterRateConsume
//            onContentsChanged()
            return if (rate > 1F) points else if (rate < 0F) 0 else afterRateToSend
        } else {
            afterRateToSend = (currentMana * rate).toInt()
            if (afterRateToSend == 0) return 0
            val tmp: Int = if (rate > 1) currentMana else if (rate < 0) 0 else afterRateToSend
            currentMana = 0
//            onContentsChanged()
            return tmp
        }
    }

    override fun fill(points: Int) {
        currentMana += points
//        onContentsChanged()
    }
}

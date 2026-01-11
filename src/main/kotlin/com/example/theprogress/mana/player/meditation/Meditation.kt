package com.example.theprogress.mana.player.meditation

import com.example.theprogress.mana.Attachments.MEDITATION
import com.example.theprogress.mana.Attachments.PLAYERMANA
import com.example.theprogress.mana.player.reborn.Reborn
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerPlayer
import kotlin.math.max
import kotlin.math.min

open class Meditation(
    override var inMeditation: Boolean = false,
    override var meditationProgress: Float = 0F,
    override var sinkingSpeed: Float = 0.001F
): IMeditation {

    // Properties
    private val baseDepthMeditation = 0.9F

    // Initializers
    companion object {
        val CODEC: MapCodec<Meditation> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.BOOL.fieldOf("inMeditation").forGetter(Meditation::inMeditation),
                Codec.FLOAT.fieldOf("meditationProgress").forGetter(Meditation::meditationProgress),
                Codec.FLOAT.fieldOf("sinkingSpeed").forGetter(Meditation::sinkingSpeed)
            ).apply(instance, ::Meditation)
        }

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, Meditation> = StreamCodec.composite(
            ByteBufCodecs.BOOL, Meditation::inMeditation,
            ByteBufCodecs.FLOAT, Meditation::meditationProgress,
            ByteBufCodecs.FLOAT, Meditation::sinkingSpeed,
            ::Meditation
        )
    }

    override fun copy(playerProperties: IMeditation) {
        this.inMeditation = playerProperties.inMeditation
        this.meditationProgress = playerProperties.meditationProgress
        this.sinkingSpeed = playerProperties.sinkingSpeed
    }

    // Important

//    override fun sendToPlayer(player: ServerPlayerEntity) {
//        RMNetworkChannel.send(PacketDistributor.PLAYER.with { player }, MeditationRebornNetwork(this.toByteArray()))
//    }

    override fun playerTick(playerIn: ServerPlayer) {
        val playerMana = playerIn.getData(PLAYERMANA)
        if (this.inMeditation) {
            if (this.meditationProgress < 1F) {
                this.meditationProgress = min(this.meditationProgress + this.sinkingSpeed, 1F)
                playerMana.meditationStart(this.baseDepthMeditation * this.meditationProgress)
            }
        }
        else{
            if (this.meditationProgress > 0F) {
                this.meditationProgress = max(this.meditationProgress - this.sinkingSpeed, 0F)
                playerMana.meditationStart(this.baseDepthMeditation * this.meditationProgress)
            }
            else{
                playerMana.meditationStop()
            }
        }
    }

    // Methods
    override fun startMeditation(playerIn: ServerPlayer) {
        this.inMeditation = true
        playerIn.setData(MEDITATION, this)
//        sendToPlayer(playerIn)
    }

    override fun stopMeditation(playerIn: ServerPlayer) {
        this.inMeditation = false
        playerIn.setData(MEDITATION, this)
//        sendToPlayer(playerIn)
    }

    fun sinkingSpeedRefresh(reborn: Reborn){
        this.sinkingSpeed = (this.sinkingSpeed / (reborn.rebornStage - 1)) * reborn.rebornStage
    }
}

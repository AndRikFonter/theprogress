package com.example.theprogress.mana.player.reborn

import com.example.theprogress.mana.Attachments.MAGICHEALTH
import com.example.theprogress.mana.Attachments.MEDITATION
import com.example.theprogress.mana.Attachments.PLAYERMANA
import com.example.theprogress.mana.Attachments.REBORN
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerPlayer
import kotlin.math.max
import kotlin.math.min

open class Reborn(
    override var rebornPrepare: Float = 0.0F,
    override var isInReborn: Boolean = false,
    override var rebornProgress: Float = 0.0F,
    override var rebornStage: Int = 1
): IReborn {

    // Initializers
    companion object {
        val CODEC: MapCodec<Reborn> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.FLOAT.fieldOf("rebornPrepare").forGetter(Reborn::rebornPrepare),
                Codec.BOOL.fieldOf("isInReborn").forGetter(Reborn::isInReborn),
                Codec.FLOAT.fieldOf("rebornProgress").forGetter(Reborn::rebornProgress),
                Codec.INT.fieldOf("rebornStage").forGetter(Reborn::rebornStage)
            ).apply(instance, ::Reborn)
        }

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, Reborn> = StreamCodec.composite(
            ByteBufCodecs.FLOAT, Reborn::rebornPrepare,
            ByteBufCodecs.BOOL, Reborn::isInReborn,
            ByteBufCodecs.FLOAT, Reborn::rebornProgress,
            ByteBufCodecs.VAR_INT, Reborn::rebornStage,
            ::Reborn
        )
    }

    override fun copy(playerProperties: IReborn) {
        this.rebornPrepare = playerProperties.rebornPrepare
        this.isInReborn = playerProperties.isInReborn
        this.rebornProgress = playerProperties.rebornProgress
        this.rebornStage = playerProperties.rebornStage
    }

    // Important

//    override fun sendToPlayer(player: ServerPlayerEntity) {
//        RMNetworkChannel.send(PacketDistributor.PLAYER.with { player }, RebornNetwork(this.toByteArray()))
//    }

    override fun playerTick(playerIn: ServerPlayer) {
        if(this.isInReborn){
            val mana = playerIn.getData(PLAYERMANA)
            this.rebornProgress += 1F / this.rebornStage
            if (this.rebornProgress >= 100F) {
                playerIn.displayClientMessage(
                    Component.literal("You have reached a new reborn level!"), true
                )
                this.rebornStage += 1
                this.rebornProgress = 0F
                this.rebornPrepare = 0F
                this.isInReborn = false
                mana.maxManaRefresh(this)
                val meditationReborn = playerIn.getData(MEDITATION)
                meditationReborn.sinkingSpeedRefresh(this)
            }
        }
        else{
            this.addPrepare(0.00001F)
        }
    }

    // Methods
    fun addPrepare(points: Float){
        rebornPrepare = min(100F, points + rebornPrepare)
    }

    fun startReborn(playerIn: ServerPlayer){
        if (this.rebornPrepare == 100F){
            this.isInReborn = true
            playerIn.getData(PLAYERMANA).blockMana()
            playerIn.displayClientMessage(
                Component.translatable("ru.rikgela.russianmagic.objects.player.reborn.Reborn is started!"),
                true
            )
            playerIn.setData(REBORN, this)
//            sendToPlayer(playerIn)
        }
        else{
            playerIn.getData(MAGICHEALTH).harmMagicHealth(100 - this.rebornPrepare.toInt())
            playerIn.displayClientMessage(
                Component.literal("You are not prepared to reborn!"), true
            )
        }
    }

    fun stopReborn(playerIn: ServerPlayer){
        this.rebornProgress = 0F
        this.rebornPrepare = max(this.rebornPrepare - 5F, 0F)
        this.isInReborn = false
        playerIn.getData(MAGICHEALTH).harmMagicHealth(this.rebornProgress.toInt())
        playerIn.getData(PLAYERMANA).allowMana()
        playerIn.displayClientMessage(
            Component.translatable("ru.rikgela.russianmagic.objects.player.reborn.Reborn is stopped!"),
            true
        )
        playerIn.setData(REBORN, this)
//        sendToPlayer(playerIn)
    }
}

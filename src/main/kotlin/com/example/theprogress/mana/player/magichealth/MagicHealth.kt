package com.example.theprogress.mana.player.magichealth

import com.example.theprogress.mana.Attachments
import com.example.theprogress.mana.Mana
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.random.Random

open class MagicHealth(
    override var curMagicHealth: Int = 1000,
    override var maxMagicHealth: Int = 1000
): IMagicHealth {

    // Properties

    private val magicDiseases: List<MobEffectInstance> = listOf(
        MobEffectInstance(MobEffects.WEAKNESS, 200, 1),
        MobEffectInstance(MobEffects.BLINDNESS, 200, 1),
        MobEffectInstance(MobEffects.WITHER, 200, 1)
    )
    val disease: MobEffectInstance get() {
        return magicDiseases.random()
    }

    private val magicRandom: Random = Random(System.nanoTime())

    // Initializers
    companion object {
        val CODEC: MapCodec<MagicHealth> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.INT.fieldOf("curMagicHealth").forGetter(MagicHealth::curMagicHealth),
                Codec.INT.fieldOf("maxMagicHealth").forGetter(MagicHealth::maxMagicHealth)
            ).apply(instance, ::MagicHealth)
        }

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, MagicHealth> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MagicHealth::curMagicHealth,
            ByteBufCodecs.VAR_INT, MagicHealth::maxMagicHealth,
            ::MagicHealth
        )
    }

    override fun copy(playerProperties: IMagicHealth) {
        this.curMagicHealth = playerProperties.curMagicHealth
        this.maxMagicHealth = playerProperties.maxMagicHealth
    }

    // Important

//    override fun sendToPlayer(player: ServerPlayerEntity) {
//        RMNetworkChannel.send(PacketDistributor.PLAYER.with { player }, MagicHealthNetwork(this.toByteArray()))
//    }

    override fun playerTick(playerIn: ServerPlayer){
        if (magicRandom.nextInt(0, maxMagicHealth + 1) > curMagicHealth){
            playerIn.addEffect(disease)
        }
    }

    // Methods
    override fun harmMagicHealth(points: Int){
        curMagicHealth = max(curMagicHealth - points, 0)
    }

    override fun healMagicHealth(points: Int){
        curMagicHealth = min(maxMagicHealth, points + curMagicHealth)
    }
}
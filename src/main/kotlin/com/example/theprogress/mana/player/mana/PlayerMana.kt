package com.example.theprogress.mana.player.mana

import com.example.theprogress.mana.Attachments.MAGICHEALTH
import com.example.theprogress.mana.Attachments.MANA
import com.example.theprogress.mana.Attachments.MEDITATION
import com.example.theprogress.mana.Attachments.PLAYERMANA
import com.example.theprogress.mana.Attachments.REBORN
import com.example.theprogress.mana.IManaTaker
import com.example.theprogress.mana.Mana
import com.example.theprogress.mana.ManaTaker
import com.example.theprogress.mana.player.reborn.Reborn
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level

class PlayerMana(
    var maxMana: Float = 1000.0F,
    private var canCast: Boolean = true,
    override var currentMana: Int = 250,
    override var baseMaxMana: Int = 1000
) : Mana(currentMana, baseMaxMana), IPlayerMana, IManaTaker {
    // Properties

    private val koef: Float = 3F / 20F
    private val sensitivity: Float = 0.1F
    private var manaTaker: ManaTaker = ManaTaker()

    override val rate: Float
        get() = manaTaker.rate

    // Initializers
    companion object {
        val CODEC: MapCodec<PlayerMana> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.FLOAT.fieldOf("maxMana").forGetter(PlayerMana::maxMana),
                Codec.BOOL.fieldOf("canCast").forGetter(PlayerMana::canCast),
                Codec.INT.fieldOf("currentMana").forGetter(PlayerMana::currentMana),
                Codec.INT.fieldOf("baseMaxMana").forGetter(PlayerMana::baseMaxMana)
            ).apply(instance, ::PlayerMana)
        }

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, PlayerMana> = StreamCodec.composite(
            ByteBufCodecs.FLOAT, PlayerMana::maxMana,
            ByteBufCodecs.BOOL, PlayerMana::canCast,
            ByteBufCodecs.VAR_INT, PlayerMana::currentMana,
            ByteBufCodecs.VAR_INT, PlayerMana::baseMaxMana,
            ::PlayerMana
        )
    }

    // Important
//    override fun sendToPlayer(player: ServerPlayerEntity) {
//        RMNetworkChannel.send(PacketDistributor.PLAYER.with { player }, PlayerManaNetwork(this.toByteArray()))
//    }

    fun maxManaRefresh(reborn: Reborn){
        maxMana = baseMaxMana.toFloat() * reborn.rebornStage
    }

    // Methods
    override fun connectToManaSpreader(
        manaSpreaderPos: BlockPos,
        levelManaSpreader: Level
    ) {
        manaTaker.connectToManaSpreader(manaSpreaderPos, levelManaSpreader)
    }

    override fun disconnectToManaSpreader() {
        manaTaker.disconnectToManaSpreader()
    }

    override fun getMana(
        points: Int,
        levelManaSpreader: Level,
        manaConsumer: BlockPos,
        sensitivity: Float
    ): Int {
        return manaTaker.getMana(points, levelManaSpreader, manaConsumer, sensitivity)
    }

    fun blockMana(){
        canCast = false
    }

    fun allowMana(){
        canCast = true
    }

    fun meditationStart(meditationDepth: Float){
        blockMana()
        manaTaker.rate = sensitivity * (meditationDepth)
    }

    fun meditationStop(){
        allowMana()
        manaTaker.rate = sensitivity
    }

    fun naturalManaBreath(playerIn: ServerPlayer){
        val reborn: Reborn = playerIn.getData(REBORN)
        if (currentMana >= maxMana * 10){
            if (!reborn.isInReborn){
                reborn.startReborn(playerIn)
            }
        }
        else{
            if (reborn.isInReborn) {
                reborn.stopReborn(playerIn)
            }
        }
        playerIn.getData(MANA)
        val meditation = playerIn.getData(MEDITATION)
        if (meditation.inMeditation) {
            val curMaxMana = maxMana * 10
            if (currentMana <= curMaxMana) {
                fill(Integer.max((curMaxMana.toInt() - currentMana) / 100, 1))
                fill(manaTaker.getMana(curMaxMana.toInt(), playerIn.level(), playerIn.blockPosition(), 1F))
            }
            else {
                val points = Integer.max(((currentMana - curMaxMana) * koef).toInt(), 1)
                consume(points)
                playerIn.setData(PLAYERMANA, this)
//                sendToPlayer(playerIn)
            }
        }
        else {
            if (currentMana <= maxMana)
                fill(Integer.max((maxMana.toInt() - currentMana) / 100, 1))
            else {
                val points = Integer.max(((currentMana - maxMana) * koef).toInt(), 1)
                consume(points)
                reborn.addPrepare(points * 0.00001F)
                playerIn.setData(PLAYERMANA, this)
//                sendToPlayer(playerIn)
            }
        }
    }

    override fun artificialConsume(points: Int, player: ServerPlayer): Boolean {
        if(canCast){
            val manaFromMagicSource = manaTaker.getMana(
                (points * 0.9F).toInt(), player.level(),
                player.blockPosition(), 1F
            )
            return if (consume(points - manaFromMagicSource)) {
                if (points > maxMana / 10) {
                    player.hurt(
                        player.level().damageSources().magic(),
                        (100F * (points - maxMana * 0.1F) / maxMana)
                    )
                    player.getData(MAGICHEALTH)
                        .harmMagicHealth((100F * (points - maxMana * 0.1F) / maxMana).toInt())
                }
                player.getData(REBORN).addPrepare(points / this.maxMana)
                player.setData(PLAYERMANA, this)
//                sendToPlayer(player)
                true
            } else {
                player.hurt(
                    player.level().damageSources().magic(),
                    (100F * (points - maxMana * 0.1F) / maxMana)
                )
                false
            }
        }
        else{
            player.displayClientMessage(Component.literal("Your mana is blocked!"), true)
            return false
        }
    }
}

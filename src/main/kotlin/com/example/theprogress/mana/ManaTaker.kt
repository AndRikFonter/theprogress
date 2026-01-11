package com.example.theprogress.mana

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import java.util.Optional
import kotlin.jvm.optionals.getOrNull
import kotlin.math.max
import kotlin.math.min


class ManaTaker(
    private var levelResourceKey: String = "",
    var spreaderPos: BlockPos? = null
) : IManaTaker {

    // Properties
        private val baseDistance = 100F

    override var rate: Float = 0F
        set(value){
            field = max(min(value, 1F), 0F)
        }

    companion object {
        val CODEC: MapCodec<ManaTaker> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.STRING.fieldOf("levelResourceKey").forGetter(ManaTaker::levelResourceKey),
                BlockPos.CODEC.optionalFieldOf("spreaderPos").forGetter{
                    Optional.ofNullable(it.spreaderPos)
                },
            ).apply(instance) { levelResourceKey, spreaderPos ->
                ManaTaker(levelResourceKey, spreaderPos.getOrNull())
            }
        }
    }

    // Important

    // Methods

    override fun connectToManaSpreader(
        manaSpreaderPos: BlockPos,
        levelManaSpreader: Level
    ) {
        val blockEntity = levelManaSpreader.getBlockEntity(manaSpreaderPos)
        if (blockEntity is IManaSpreader) {
            spreaderPos = manaSpreaderPos
            levelResourceKey = levelManaSpreader.dimension().toString()
        }
    }

    override fun disconnectToManaSpreader() {
        spreaderPos = null
        levelResourceKey = ""
    }

    fun getRate(manaConsumer: BlockPos, sensitivity: Float): Float {
        return if(spreaderPos != null) {
            val distance = spreaderPos?.distSqr(manaConsumer)?.toFloat() ?: 0F
//            val distance = kotlin.math.sqrt(
//                spreaderPos!!.distSqr(
//                    Vec3i(
//                        manaConsumer.x,
//                        manaConsumer.y,
//                        manaConsumer.z
//                    )
//                )
//            ).toFloat()
            val rate = sensitivity * if (distance >= baseDistance) {1F / distance} else {1F - distance / baseDistance}
            java.lang.Float.max(min(rate, 1F), 0F)
        } else {
            0F
        }
    }

    override fun getMana(
        points: Int,
        levelManaSpreader: Level,
        manaConsumer: BlockPos,
        sensitivity: Float
    ): Int {
        return spreaderPos?.let {
            val blockEntity = levelManaSpreader.getBlockEntity(it)
            if (blockEntity is IManaSpreader) {
                if (manaConsumer != BlockPos(-1, -1, -1)) {
                    return blockEntity.spread(points, getRate(manaConsumer, sensitivity))
                }
            } else {
                //Todo action if cannot get tileEntity
            }
            return 0
        } ?: 0
    }
}

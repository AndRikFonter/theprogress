package com.example.theprogress.mana.player.mana

import com.example.theprogress.TheProgress.Companion.MODID
import com.example.theprogress.mana.Attachments
import net.minecraft.client.Minecraft
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent
import net.neoforged.neoforge.client.gui.VanillaGuiLayers
import java.lang.Float.min

@EventBusSubscriber(modid = MODID, value = [Dist.CLIENT])
object HudRenderer {
    private var displayMana = 0f

    @SubscribeEvent
    @JvmStatic
    fun onRenderGui(event: RenderGuiLayerEvent.Post) {
        // Проверка через name и PLAYER_HEALTH по вашему указанию
        if (event.name != VanillaGuiLayers.PLAYER_HEALTH) return

        val mc = Minecraft.getInstance()
        val player = mc.player ?: return
        if (mc.options.hideGui) return

        val mana = player.getData(Attachments.PLAYERMANA)
        val graphics = event.guiGraphics

        displayMana = lerp(displayMana, mana.currentMana.toFloat(), 0.15f)

        // Логика позиционирования над первой строкой чата
        val chatScale = mc.options.chatScale().get().toFloat()
        val lineHeight = (9 * chatScale).toInt()

        val x = 2
        // graphics.guiHeight() возвращает актуальную высоту
        val y = graphics.guiHeight() - lineHeight - 25

        val barWidth = 100
        val barHeight = 8

        // Логика цвета (как мы обсуждали ранее)
        var currentColor = 0xFF22AAFFL
        if (mana.currentMana > mana.baseMaxMana) {
            val lambda = min((mana.currentMana - mana.baseMaxMana).toFloat() / (mana.baseMaxMana * 1.5f), 1f)
            val r = (34 + (190 - 34) * lambda).toLong()
            val g = (170 + (40 - 170) * lambda).toLong()
            val b = 255L
            currentColor = (0xFF shl 24).toLong() or (r shl 16) or (g shl 8) or b
        }

        val colorTop = currentColor.toInt()
        val colorBottom = multiplyColor(currentColor, 0.5f)

        // Фон
        graphics.fill(x, y, x + barWidth, y + barHeight, 0xAA000000.toInt())

        // Заполнение полоски
        val filledWidth = ((displayMana / mana.baseMaxMana.coerceAtLeast(1)) * barWidth).toInt()
            .coerceIn(0, barWidth)

        if (filledWidth > 0) {
            graphics.fillGradient(x, y, x + filledWidth, y + barHeight, colorTop, colorBottom)
            // Блик сверху
            graphics.fill(x, y, x + filledWidth, y + 2, 0x44FFFFFF.toInt())
        }

        // Текст маны
        val text = "${mana.currentMana} / ${mana.baseMaxMana}"
        graphics.drawString(mc.font, text, x + 2, y - 10, colorTop, true)
    }

    private fun lerp(start: Float, end: Float, fraction: Float): Float = start + fraction * (end - start)

    private fun multiplyColor(color: Long, factor: Float): Int {
        val a = (color shr 24) and 0xFF
        val r = (((color shr 16) and 0xFF) * factor).toLong()
        val g = (((color shr 8) and 0xFF) * factor).toLong()
        val b = ((color and 0xFF) * factor).toLong()
        return ((a shl 24) or (r shl 16) or (g shl 8) or b).toInt()
    }
}
package com.example.theprogress

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.gui.ConfigurationScreen
import net.neoforged.neoforge.client.gui.IConfigScreenFactory

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(
    value = TheProgress.MODID,
    dist = [Dist.CLIENT]
) // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = TheProgress.MODID, value = [Dist.CLIENT])
class TheProgressClient(container: ModContainer) {
    init {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(
            IConfigScreenFactory::class.java,
            IConfigScreenFactory { mod: ModContainer, parent: Screen -> ConfigurationScreen(mod, parent) })
    }

    companion object {
        @SubscribeEvent
        fun onClientSetup(event: FMLClientSetupEvent) {
            // Some client setup code
            TheProgress.LOGGER.info("HELLO FROM CLIENT SETUP")
            TheProgress.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().user.name)
        }
    }
}

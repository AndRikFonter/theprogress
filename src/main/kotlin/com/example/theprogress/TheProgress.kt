package com.example.theprogress

import com.example.theprogress.mana.Attachments.ATTACHMENT_TYPES
import com.example.theprogress.mana.ManaSyncPayload
import com.example.theprogress.mana.player.mana.SyncPayload as PlayerManaSyncPayload
import com.example.theprogress.mana.player.magichealth.SyncPayload as MagicHealthSyncPayload
import com.example.theprogress.mana.player.meditation.SyncPayload as MeditationSyncPayload
import com.example.theprogress.mana.player.reborn.SyncPayload as RebornSyncPayload
import com.mojang.logging.LogUtils
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import net.neoforged.neoforge.registries.*
import org.slf4j.Logger
import java.util.function.Supplier


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(TheProgress.MODID)
class TheProgress(modEventBus: IEventBus, modContainer: ModContainer) {
    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    init {
        // Register the commonSetup method for modloading
        modEventBus.addListener { event: FMLCommonSetupEvent -> this.commonSetup(event) }
        modEventBus.addListener { event: RegisterPayloadHandlersEvent ->
            val registrar: PayloadRegistrar = event.registrar("1")
            registrar.playToClient(
                ManaSyncPayload.ID,
                ManaSyncPayload.CODEC,
                ManaSyncPayload::handle
            )
            registrar.playToClient(
                PlayerManaSyncPayload.ID,
                PlayerManaSyncPayload.CODEC,
                PlayerManaSyncPayload::handle
            )
            registrar.playToClient(
                MagicHealthSyncPayload.ID,
                MagicHealthSyncPayload.CODEC,
                MagicHealthSyncPayload::handle
            )
            registrar.playToClient(
                MeditationSyncPayload.ID,
                MeditationSyncPayload.CODEC,
                MeditationSyncPayload::handle
            )
            registrar.playToClient(
                RebornSyncPayload.ID,
                RebornSyncPayload.CODEC,
                RebornSyncPayload::handle
            )
        }
        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus)
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus)
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus)
        // Register the Deferred Register to the mod event bus so attachment types get registered
        ATTACHMENT_TYPES.register(modEventBus)

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (TheProgress) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this)

        // Register the item to a creative tab
        modEventBus.addListener { event: BuildCreativeModeTabContentsEvent ->
            this.addCreative(
                event
            )
        }

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC)
    }

    private fun commonSetup(event: FMLCommonSetupEvent) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP")

        if (Config.LOG_DIRT_BLOCK.asBoolean) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT))
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.asInt)

        Config.ITEM_STRINGS.get().forEach { item: String -> LOGGER.info("ITEM >> {}", item) }
    }

    // Add the example block item to the building blocks tab
    private fun addCreative(event: BuildCreativeModeTabContentsEvent) {
        if (event.tabKey === CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(EXAMPLE_BLOCK_ITEM)
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting")
    }

    companion object {
        // Define mod id in a common place for everything to reference
        const val MODID: String = "theprogress"

        // Directly reference a slf4j logger
        val LOGGER: Logger = LogUtils.getLogger()

        // Create a Deferred Register to hold Blocks which will all be registered under the "theprogress" namespace
        val BLOCKS: DeferredRegister.Blocks = DeferredRegister.createBlocks(MODID)

        // Create a Deferred Register to hold Items which will all be registered under the "theprogress" namespace
        val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(MODID)

        // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "theprogress" namespace
        val CREATIVE_MODE_TABS: DeferredRegister<CreativeModeTab> =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID)

        // Create a Deferred Register to hold AttachmentTypes which will all be registered under the "theprogress" namespace
//        val ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID)
//
//        // Creates a new Attachment with the id "theprogress:mana", combining the namespace and path
//        val MANA: DeferredHolder<AttachmentType<*>, AttachmentType<Mana>> = ATTACHMENT_TYPES.register("mana") { ->
//            AttachmentType.builder(::Mana)
//                .serialize(Mana.CODEC)
////                .copyOnDeath() // Автоматически заменяет PlayerEvent.Clone!
//                .build()
//        }

        // Creates a new Block with the id "theprogress:example_block", combining the namespace and path
        val EXAMPLE_BLOCK: DeferredBlock<Block> =
            BLOCKS.registerSimpleBlock("example_block") { p: BlockBehaviour.Properties ->
                p.mapColor(
                    MapColor.STONE
                )
            }

        // Creates a new BlockItem with the id "theprogress:example_block", combining the namespace and path
        val EXAMPLE_BLOCK_ITEM: DeferredItem<BlockItem> = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK)

        // Creates a new food item with the id "theprogress:example_id", nutrition 1 and saturation 2
        val EXAMPLE_ITEM: DeferredItem<Item> =
            ITEMS.registerSimpleItem("vlad_brain") { p: Item.Properties ->
                p.food(
                    FoodProperties.Builder()
                        .alwaysEdible().nutrition(1).saturationModifier(2f).build()
                )
            }

        // Creates a creative tab with the id "theprogress:example_tab" for the example item, that is placed after the combat tab
        val EXAMPLE_TAB: DeferredHolder<CreativeModeTab, CreativeModeTab> =
            CREATIVE_MODE_TABS.register("the_progress", Supplier {
                CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.theprogress")) //The language key for the title of your CreativeModeTab
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon { EXAMPLE_ITEM.get().defaultInstance }
                    .displayItems { parameters: ItemDisplayParameters, output: CreativeModeTab.Output ->
                        output.accept(
                            EXAMPLE_ITEM.get()
                        ) // Add the example item to the tab. For your own tabs, this method is preferred over the event
                    }.build()
            })
    }
}

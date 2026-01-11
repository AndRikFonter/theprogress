package com.example.theprogress.mana

import com.example.theprogress.TheProgress.Companion.MODID
import com.example.theprogress.mana.player.magichealth.MagicHealth
import com.example.theprogress.mana.player.mana.PlayerMana
import com.example.theprogress.mana.player.meditation.Meditation
import com.example.theprogress.mana.player.reborn.Reborn
import net.minecraft.world.entity.EntityAttachment
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries

object Attachments {
    val ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MODID)

    val MANA: DeferredHolder<AttachmentType<*>, AttachmentType<Mana>> = ATTACHMENT_TYPES.register("mana") { ->
        AttachmentType.builder(::Mana)
            .serialize(Mana.CODEC)
            .sync(Mana.STREAM_CODEC)
//                .copyOnDeath() // Автоматически заменяет PlayerEvent.Clone!
            .build()
    }

    val PLAYERMANA: DeferredHolder<AttachmentType<*>, AttachmentType<PlayerMana>> = ATTACHMENT_TYPES.register("playermana") { ->
        AttachmentType.builder(::PlayerMana)
            .serialize(PlayerMana.CODEC)
            .sync(PlayerMana.STREAM_CODEC)
//                .copyOnDeath() // Автоматически заменяет PlayerEvent.Clone!
            .build()
    }

    val MAGICHEALTH: DeferredHolder<AttachmentType<*>, AttachmentType<MagicHealth>> = ATTACHMENT_TYPES.register("magichealth") { ->
        AttachmentType.builder(::MagicHealth)
            .serialize(MagicHealth.CODEC)
            .sync(MagicHealth.STREAM_CODEC)
//                .copyOnDeath() // Автоматически заменяет PlayerEvent.Clone!
            .build()
    }

    val MEDITATION: DeferredHolder<AttachmentType<*>, AttachmentType<Meditation>> = ATTACHMENT_TYPES.register("meditation") { ->
        AttachmentType.builder(::Meditation)
            .serialize(Meditation.CODEC)
            .sync(Meditation.STREAM_CODEC)
//                .copyOnDeath() // Автоматически заменяет PlayerEvent.Clone!
            .build()
    }

    val REBORN: DeferredHolder<AttachmentType<*>, AttachmentType<Reborn>> = ATTACHMENT_TYPES.register("reborn") { ->
        AttachmentType.builder(::Reborn)
            .serialize(Reborn.CODEC)
            .sync(Reborn.STREAM_CODEC)
//                .copyOnDeath() // Автоматически заменяет PlayerEvent.Clone!
            .build()
    }
}

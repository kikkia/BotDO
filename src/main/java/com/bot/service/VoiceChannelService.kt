package com.bot.service

import com.bot.db.entities.VoiceChannelEntity
import com.bot.db.repositories.VoiceChannelRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
open class VoiceChannelService(private val voiceChannelRepository: VoiceChannelRepository) {

    open fun getById(id: String): Optional<VoiceChannelEntity> {
        return voiceChannelRepository.findById(id)
    }

    open fun newChannel(id: String, name: String, guildId: String) : VoiceChannelEntity {
        return voiceChannelRepository.save(VoiceChannelEntity(id, guildId, name, false))
    }

    open fun save(channelEntity: VoiceChannelEntity) : VoiceChannelEntity {
        return voiceChannelRepository.save(channelEntity)
    }

    open fun delete(id: String) {
        voiceChannelRepository.deleteById(id)
    }
}
package com.bot.db.repositories

import com.bot.db.entities.VoiceChannelEntity
import org.springframework.data.repository.CrudRepository

interface VoiceChannelRepository : CrudRepository<VoiceChannelEntity, String> {
}
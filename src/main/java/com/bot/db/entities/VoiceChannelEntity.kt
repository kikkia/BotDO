package com.bot.db.entities

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "voice_channel")
data class VoiceChannelEntity(
        @Id
        val id: String,
        @Column(name = "guild")
        val guild: String,
        var name: String,
        var war: Boolean)
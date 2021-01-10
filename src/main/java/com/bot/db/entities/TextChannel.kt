package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "text_channel")
data class TextChannel(
        @Id
        val id: String,
        @ManyToOne
        @JoinColumn(name = "id")
        val guild: Guild,
        val name: String,
        val announcment: Boolean,
        val persistentScrolls: Boolean)
package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "text_channel")
data class TextChannel(
        @Id
        val id: String,
        @ManyToOne
        @JoinColumn(name = "guild")
        val guild: Guild,
        var name: String,
        var announcement: Boolean,
        @Column(name="persistent_scrolls")
        var persistentScrolls: Boolean)
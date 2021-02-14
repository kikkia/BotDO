package com.bot.db.entities

import com.bot.models.EventType
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "event")
data class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    @ManyToOne
    @JoinColumn(name = "guild_id")
    val guild: Guild,
    @ManyToOne
    @JoinColumn(name = "author")
    val author: User,
    @Column(name = "next_time")
    val nextTime: Timestamp,
    @Column(name = "event_type")
    val eventType: Int) {

    fun getEventType() : EventType? {
        return EventType.fromInt(eventType)
    }
}
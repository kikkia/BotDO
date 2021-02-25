package com.bot.db.entities

import com.bot.models.EventType
import org.joda.time.Duration
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "event")
data class EventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    @ManyToOne
    @JoinColumn(name = "guild_id")
    var guild: GuildEntity,
    @ManyToOne
    @JoinColumn(name = "author")
    var author: UserEntity,
    @Column(name = "next_time")
    var nextTime: Timestamp,
    @Column(name = "event_type")
    var eventType: Int,
    @Column(name = "name")
    var name: String,
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "event_id")
    var roles: List<EventRoleEntity>) {

    fun getEventType() : EventType? {
        return EventType.fromInt(eventType)
    }

    fun getDurationUntilEvent() : Duration {
        return Duration(nextTime.toInstant().toEpochMilli() - Instant.now().toEpochMilli());
    }
}
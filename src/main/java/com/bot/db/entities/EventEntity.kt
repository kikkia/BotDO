package com.bot.db.entities

import com.bot.models.EventType
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "event")
data class EventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    @ManyToOne
    @JoinColumn(name = "guild_id")
    val guild: GuildEntity,
    @ManyToOne
    @JoinColumn(name = "author")
    val author: UserEntity,
    @Column(name = "next_time")
    @Temporal(TemporalType.TIMESTAMP)
    val nextTime: Timestamp,
    @Column(name = "event_type")
    val eventType: Int,
    @Column(name = "name")
    val name: String,
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "event")
    var roles: List<EventRoleEntity>) {

    fun getEventType() : EventType? {
        return EventType.fromInt(eventType)
    }
}
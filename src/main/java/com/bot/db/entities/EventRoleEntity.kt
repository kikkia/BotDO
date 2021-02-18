package com.bot.db.entities

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "event_roles")
data class EventRoleEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,
        @ManyToOne
        val event: EventEntity,
        @Column(name = "role_id")
        val roleId: String) : Serializable
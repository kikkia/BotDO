package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "event_role")
data class EventRoleEntity(
        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "event_id")
        val event: EventEntity,
        @Column(name = "role_id")
        val roleId: String)
package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "war_dm_signups")
class WarDmSignupEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,
        @Column(name = "message_id")
        val messageId: String,
        @Column(name = "user_id")
        val userId: String,
        @ManyToOne
        @JoinColumn(name = "war_id")
        var warEntity: WarEntity) {
        @Column(name = "active")
        var active = true
}
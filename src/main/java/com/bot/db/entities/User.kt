package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
        @Id
        var id: String,
        @Column(nullable = false)
        var name: String,
        @Column(name = "family_name",
                nullable = true)
        var familyName: String
)
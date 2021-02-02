package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
        @Id
        var id: String,
        @Column(nullable = false)
        var name: String
) {

        @OneToOne(mappedBy = "user",
                fetch = FetchType.LAZY)
        var inventory: ScrollInventoryEntity? = null

        @Column(name = "family_name",
                nullable = true)
        var familyName: String? = null

        fun getEffectiveName() : String {
                return familyName ?: name
        }
}
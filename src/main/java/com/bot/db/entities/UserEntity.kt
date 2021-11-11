package com.bot.db.entities

import net.dv8tion.jda.api.entities.User
import javax.persistence.*

@Entity
@Table(name = "users")
data class UserEntity(
        @Id
        var id: String,
        @Column(nullable = false)
        var name: String) {

        @OneToOne(mappedBy = "user",
                fetch = FetchType.LAZY)
        var inventory: ScrollInventoryEntity? = null

        @Column(name = "family_name",
                nullable = true)
        var familyName: String? = null

        @OneToOne(mappedBy = "userEntity", cascade = [CascadeType.ALL])
        var gearset: GearsetEntity? = null

        @Column(name = "avatar")
        var avatar: String? = null

        @Column(name = "default_region", nullable = false)
        var defaultRegion: String = "NA"

        fun getEffectiveName() : String {
                return familyName ?: name
        }

        companion object {
                fun from(user: User) : UserEntity {
                        return UserEntity(user.id, user.name)
                }
        }
}
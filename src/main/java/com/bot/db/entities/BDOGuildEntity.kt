package com.bot.db.entities

import com.bot.models.WarDay
import com.fasterxml.jackson.annotation.JsonIgnore
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "bdo_guild")
class BDOGuildEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    @Column(name = "name")
    val name: String,
    @Column(name = "region")
    val region: String,
    @Column(name = "last_scan")
    var last_scan: Timestamp) {
    @JsonIgnore
    @OneToOne(mappedBy = "bdoGuild")
    var discordGuild: GuildEntity? = null
    @JsonIgnore
    @Column(name = "war_days")
    var warDays: Int? = null
    @Column(name = "master_family")
    var master: Int? = null
    @Column(name = "active")
    var active = true

    fun getWarDays() : List<WarDay> {
        return if (warDays == null) {
            listOf()
        } else {
            WarDay.getAllDays(warDays!!)
        }
    }
}
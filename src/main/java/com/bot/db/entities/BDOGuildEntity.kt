package com.bot.db.entities

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
    var last_scan: Timestamp,
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "guild", cascade = [CascadeType.ALL])
    var members: List<GuildMembershipEntity>,
    @OneToOne
    @JoinColumn(name = "master_family")
    var master: FamilyEntity?)
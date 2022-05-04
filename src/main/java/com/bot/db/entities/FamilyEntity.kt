package com.bot.db.entities

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "family")
class FamilyEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    @Column(name = "external_id")
    var externalId: String,
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "family", cascade = [CascadeType.ALL])
    var memberships: List<GuildMembershipEntity>,
    @Column(name = "name")
    var name: String,
    @Column(name = "region")
    val region: String,
    @Column(name = "last_updated")
    var lastUpdated: Timestamp,
    @Column(name = "private")
    var private: Boolean)
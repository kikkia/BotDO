package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "family")
class FamilyEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    @Column(name = "external_id")
    val externalId: String,
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "family", cascade = [CascadeType.ALL])
    var memberships: List<GuildMembershipEntity>,
    @Column(name = "family_name")
    var name: String,
    @Column(name = "region")
    val region: String,
    @OneToMany(fetch = FetchType.LAZY)
    var characters: List<CharacterEntity>)
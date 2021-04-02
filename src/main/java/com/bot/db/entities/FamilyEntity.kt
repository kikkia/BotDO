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
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "guild")
    var guild: BDOGuildEntity,
    @Column(name = "family_name")
    var name: String,
    @Column(name = "region")
    val region: String,
    @OneToMany
    var characters: List<CharacterEntity>)
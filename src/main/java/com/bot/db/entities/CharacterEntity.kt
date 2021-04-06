package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "character")
class CharacterEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    @Column(name = "name")
    val name: String,
    @ManyToOne
    @JoinColumn(name = "family")
    val family: FamilyEntity,
    @Column(name = "class")
    val characterClass: String,
    @Column(name = "level")
    var level: Int,
    @Column(name = "main")
    var main: Boolean
)
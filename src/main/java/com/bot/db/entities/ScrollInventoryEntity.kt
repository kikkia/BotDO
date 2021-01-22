package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "scroll_inventory")
data class ScrollInventoryEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,
        @OneToOne
        @JoinColumn(name = "user_id")
        val user: User,
        val redNose: Int,
        val aRedNose: Int,
        val giath: Int,
        val aGiath: Int,
        val bheg: Int,
        val aBheg: Int,
        val moghulis: Int,
        val agrakhan: Int,
        val narc: Int,
        val aNarc: Int,
        val ronin: Int,
        val aRonin: Int,
        val dim: Int,
        val aDim: Int,
        val muskan: Int,
        val aMuskan: Int,
        val hexe: Int,
        val aHexe: Int,
        val ahib: Int,
        val aAhib: Int,
        val urugon: Int,
        val aUrugon: Int,
        val putrum: Int,
        val titium: Int,
        val aTitium: Int,
        val arc: Int,
        val cartian: Int,
        val pilaFe: Int,
        val voodoo: Int)
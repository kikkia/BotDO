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
        @Column(name = "red_nose")
        val redNose: Int,
        @Column(name = "a_red_nose")
        val aRedNose: Int,
        val giath: Int,
        @Column(name = "a_giath")
        val aGiath: Int,
        val bheg: Int,
        @Column(name = "a_bheg")
        val aBheg: Int,
        val moghulis: Int,
        val agrakhan: Int,
        val narc: Int,
        @Column(name = "a_narc")
        val aNarc: Int,
        val ronin: Int,
        @Column(name = "a_ronin")
        val aRonin: Int,
        val dim: Int,
        @Column(name = "a_dim")
        val aDim: Int,
        val muskan: Int,
        @Column(name = "a_muskan")
        val aMuskan: Int,
        val hexe: Int,
        @Column(name = "a_hexe")
        val aHexe: Int,
        val ahib: Int,
        @Column(name = "a_ahib")
        val aAhib: Int,
        val urugon: Int,
        @Column(name = "a_urugon")
        val aUrugon: Int,
        val puturum: Int,
        val titium: Int,
        @Column(name = "a_titium")
        val aTitium: Int,
        val arc: Int,
        val cartian: Int,
        @Column(name = "pila_fe")
        val pilaFe: Int,
        val voodoo: Int)
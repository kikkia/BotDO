package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "gearset")
class GearsetEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val userEntity: UserEntity
) {
    @Column(name = "ap")
    var ap: Int? = null
    @Column(name = "awk_ap")
    var awkAp: Int? = null
    @Column(name = "dp")
    var dp: Int? = null
    @Column(name = "lvl")
    var level: Int? = null
    @Column(name = "axe")
    var axe: Int? = null
    @Column(name = "class")
    var characterClass: String? = null
    @Column(name = "class_state")
    var classState: Int? = null
    @Column(name = "planner_link")
    var plannerLink: String? = null
    @Column(name = "gear_img_link")
    var gearImgLink: String? = null
}
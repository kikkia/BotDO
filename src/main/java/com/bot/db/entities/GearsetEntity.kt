package com.bot.db.entities

import com.bot.models.ClassState
import javax.persistence.*
import kotlin.math.max

@Entity
@Table(name = "gearset")
class GearsetEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    @OneToOne
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

    fun getGearScore() : Int {
        if (dp == null) {
            return 0
        }
        val mainAp = if (ap != null) ap else 0
        val awakeningAp = if (awkAp != null) awkAp else 0
        return max(mainAp!!, awakeningAp!!) + dp!!
    }

    fun getClassName() : String {
        if (characterClass == null) {
            return "unknown"
        }
        if (classState == null) {
            return characterClass!!
        }
        return if (classState == ClassState.SUCCESSION.id) "Succ $characterClass" else "Awk $characterClass"
    }
}
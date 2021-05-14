package com.bot.models

import java.util.Arrays.asList

// Stateful represents if the class can go awk/succ
enum class BdoClass(val display: String, val aliases: List<String>, val stateful: Boolean) {
    WARRIOR("Warrior", listOf("zoomy boi", "fps monkey"), true),
    RANGER("Ranger", emptyList(), true),
    SORCERESS("Sorceress", listOf("sorc"), true),
    BERSERKER("Berzerker", listOf("zerker"), true),
    TAMER("Tamer", emptyList(), true),
    MUSA("Musa", emptyList(), true),
    MAEHWA("Maehwa", emptyList(), true),
    VALKYRIE("Valkyrie", listOf("valk"), true),
    KUNOICHI("Kunoichi", listOf("kuno"), true),
    NINJA("Ninja", emptyList(), true),
    WIZARD("Wizard", listOf("wiz"), true),
    WITCH("Witch", emptyList(), true),
    DARKKNIGHT("Dark Knight", listOf("dk"), true),
    STRIKER("Striker", listOf("ook"), true),
    MYSTIC("Mystic", emptyList(), true),
    ARCHER("Archer", emptyList(), false),
    LAHN("Lahn", emptyList(), true),
    SHAI("Shai", listOf("loli"), false),
    GAURDIAN("Gaurdian", listOf("thicc bitch", "thighs", "boobs"), true),
    HASHASHIN("Hashashin", listOf("hash"), true),
    NOVA("Nova", emptyList(), true),
    SAGE("Sage", listOf("sadge"), true);

    companion object {
        fun getClassFromName(name: String) : BdoClass? {
            val cleanedName = name.trim().toLowerCase()
            for (c in values()) {
                if (c.aliases.contains(cleanedName) || c.display.toLowerCase() == cleanedName) {
                    return c
                }
            }
            return null
        }
    }
}
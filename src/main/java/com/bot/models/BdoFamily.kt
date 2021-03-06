package com.bot.models

import java.util.*

data class BdoFamily(val name: String, val id : String, val guild: String?, val private: Boolean = false) {

    var characters: List<BdoCharacter> = listOf()

    override fun hashCode(): Int {
        return Objects.hashCode(name)
    }

    /**
     * Apparently families can have multiple external targets that lead to profile
     */
    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as BdoFamily

        return other.name == this.name
    }
}
package com.bot.models

import java.util.*

data class BdoFamilyId(val name: String, val id : String, val guild: String?) {
    override fun hashCode(): Int {
        return Objects.hashCode(name)
    }

    /**
     * Apparently families can have multiple external targets that lead to profile
     */
    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as BdoFamilyId

        return other.name == this.name
    }
}
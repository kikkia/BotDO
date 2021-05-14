package com.bot.models

enum class ClassState(val id: Int, val aliases: List<String>) {
    AWAKENING(1, listOf("awk", "awakening", "awak", "awaken", "awakened")),
    SUCCESSION(2, listOf("succ", "succession"));

    companion object {
        fun getState(input: String) : ClassState? {
            for(s in values()) {
                if (s.aliases.contains(input.trim().toLowerCase())) {
                    return s
                }
            }
            return null
        }
    }
}
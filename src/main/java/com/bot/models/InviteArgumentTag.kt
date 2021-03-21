package com.bot.models

import java.lang.Integer.parseInt

enum class InviteArgumentTag(val tag: String) {
    ROLES("-r "),
    WELCOME("-w "),
    GUILD("-g "),
    USES("-u "),
    CHANNEL("-c "),
    RECRUIT("-rec ");

    companion object {
        fun getRoleNames(input: String) : List<String> {
            if (!input.contains(ROLES.tag)) {
                return emptyList()
            }
            // Return all comma separated names from arg
            return cleanToFirstArg(input.split(ROLES.tag)[1]).split(", ")
        }

        fun getWelcomeMessage(input: String) : String? {
            if (!input.contains(WELCOME.tag)) {
                return null
            }
            return cleanToFirstArg(input.split(WELCOME.tag)[1])
        }

        fun getUses(input: String) : Int {
            if (!input.contains(USES.tag)) {
                return 1
            }
            return parseInt(cleanToFirstArg(input.split(USES.tag)[1]))
        }

        fun getGuildPrefix(input: String) : String? {
            if (!input.contains(GUILD.tag)) {
                return null
            }
            return cleanToFirstArg(input.split(GUILD.tag)[1])
        }

        fun getRecruit(input: String) : Boolean {
            return input.contains(RECRUIT.tag)
        }

        /**
         * This function takes an input string that has already been split such that the arguments of the tag you want
         * are the beginning of the string. Then it goes through all possible tags and cleans out those args using splits.
         * @return A string cleaned to just the first args
         */
        private fun cleanToFirstArg(input: String) : String {
            var toReturn = input
            for (tag in values()) {
                toReturn = toReturn.split(tag.tag)[0]
            }
            return toReturn.trim()
        }
    }
}
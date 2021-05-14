package com.bot.models

import com.bot.exceptions.InvalidGearFieldException
import java.lang.Integer.parseInt
import java.lang.NumberFormatException

enum class GearArgumentTag(val tag: String) {
    AP("ap:"),
    DP("dp:"),
    AWKAP("awk:"),
    LEVEL("level:"),
    AXE("axe:"),
    CLASS("class:"),
    STATE("state:"),
    PLANNER("planner:"),
    FAMILY("family:");

    companion object {
        fun getAp(input: String) : Int? {
            if (!input.contains(AP.tag)) {
                return null
            }
            try {
                val ap = parseInt(cleanToFirstArg(input.split(AP.tag)[1]))
                if (ap < 0) {
                    throw InvalidGearFieldException("Negative ap? I mean, I know you are a gearlet but come on...")
                } else if (ap > 350) {
                    throw InvalidGearFieldException("$ap ap? Bet..")
                }
                return ap
            } catch (e: NumberFormatException) {
                throw InvalidGearFieldException("Value passed for ap must be an number. (e.g: ap:269)")
            }
        }

        fun getDp(input: String) : Int? {
            if (!input.contains(DP.tag)) {
                return null
            }
            try {
                val dp = parseInt(cleanToFirstArg(input.split(DP.tag)[1]))
                if (dp < 0) {
                    throw InvalidGearFieldException("Negative dp? I mean, I know you are a gearlet but come on...")
                } else if (dp > 600) {
                    throw InvalidGearFieldException("$dp dp? Bet..")
                }
                return dp
            } catch (e: NumberFormatException) {
                throw InvalidGearFieldException("Value passed for dp must be an number. (e.g: dp:326)")
            }
        }

        fun getAwkAp(input: String) : Int? {
            if (!input.contains(AWKAP.tag)) {
                return null
            }
            try {
                val ap = parseInt(cleanToFirstArg(input.split(AWKAP.tag)[1]))
                if (ap < 0) {
                    throw InvalidGearFieldException("Negative ap? I mean, I know you are a gearlet but come on...")
                } else if (ap > 350) {
                    throw InvalidGearFieldException("$ap ap? Bet..")
                }
                return ap
            } catch (e: NumberFormatException) {
                throw InvalidGearFieldException("Value passed for awk ap must be an number. (e.g: awk:269)")
            }
        }

        fun getLevel(input: String) : Int? {
            if (!input.contains(LEVEL.tag)) {
                return null
            }
            try {
                val level = parseInt(cleanToFirstArg(input.split(LEVEL.tag)[1]))
                if (level < 1) {
                    throw InvalidGearFieldException("Come on, your level is higher than that...")
                } else if (level > 69) {
                    throw InvalidGearFieldException("How long did level $level take at trees? Yeah not buying it.")
                }
                return level
            } catch (e: NumberFormatException) {
                throw InvalidGearFieldException("Value passed for level must be an number. (e.g: level:62)")
            }
        }

        fun getAxe(input: String) : Int? {
            if (!input.contains(AXE.tag)) {
                return null
            }
            val cleaned = cleanToFirstArg(input.split(AXE.tag)[1])
                    .replace("+", "").toLowerCase()
            if (cleaned == "i" || cleaned == "pri") {
                return 16
            } else if (cleaned == "ii" || cleaned == "duo") {
                return 17
            } else if (cleaned == "iii" || cleaned == "tri") {
                return 18
            } else if (cleaned == "iv" || cleaned == "tet") {
                return 19
            } else if (cleaned == "v" || cleaned == "pen") {
                return 20
            }
            try {
                val enhancement = parseInt(cleaned)
                if (enhancement > 20) {
                    throw InvalidGearFieldException("You got a +$enhancement axe? RNG CARRIED!")
                } else if (enhancement < 0) {
                    throw InvalidGearFieldException("Negative axe enhancement? Really?")
                }
                return enhancement
            } catch (e : NumberFormatException) {
                throw InvalidGearFieldException("Value for axe is incorrect, please use +1-15 or pri, duo, etc")
            }
        }

        fun getClass(input: String): BdoClass? {
            if (!input.contains(CLASS.tag)) {
                return null
            }
            return BdoClass.getClassFromName(cleanToFirstArg(input.split(CLASS.tag)[1]))
                    ?: throw InvalidGearFieldException("Class not found, please check that its spelled correctly")
        }

        fun getState(input: String) : ClassState? {
            if (!input.contains(STATE.tag)) {
                return null
            }
            return ClassState.getState(cleanToFirstArg(input.split(STATE.tag)[1]))
                    ?: throw InvalidGearFieldException("State not found, please put awk or succ")
        }

        fun getPlannerLink(input: String) : String? {
            if (!input.contains(PLANNER.tag)) {
                return null
            }
            val url = cleanToFirstArg(input.split(PLANNER.tag)[1])
            if (url.startsWith("https://bdoplanner.com/")) {
                return url
            } else {
                throw InvalidGearFieldException("Value passed for BDOPlanner url needs to be a url for a BDOPlanner build.")
            }
        }

        fun getFamilyName(input: String) : String? {
            return if (!input.contains(FAMILY.tag)) null else cleanToFirstArg(input.split(FAMILY.tag)[1])
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
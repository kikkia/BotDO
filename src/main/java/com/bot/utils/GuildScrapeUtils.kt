package com.bot.utils

import com.bot.models.BdoCharacter
import com.bot.models.BdoFamily
import com.bot.models.Region
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashSet
import kotlin.streams.asStream

/**
 * Utility class to help with webscraping the bdo guild directory
 */
class GuildScrapeUtils {

    companion object {
        private val client = OkHttpClient()
        //(guildName=)(.+)&region=
        private val GUILD_NAME_REGEX = Regex("(guildName=)(.+)&region=",
                setOf(RegexOption.MULTILINE))
        private val USER_PROFILE_REGEX = Regex("profileTarget=(.+)\">(.+)</a>")
        private val USER_PRIVATE_REGEX = Regex("<a href=\"javascript:void\\(0\\)\">Private</a>")

        private val CHARACTER_INFO_REGEX = Regex("<div class=\"character_txt\">(.+)</div>",
                setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL))
        private val CHARACTER_NAME_REGEX = Regex("<p class=\"character_name\">(.+)</p>",
                setOf(RegexOption.MULTILINE))
        // Both class and level can be used with the first 2 matches of this regex
        private val CHARACTER_CLASS_LEVEL_REGEX = Regex("<em>(.+)</em>")
        private val CHARACTER_MAIN_REGEX = Regex("<span class=\"selected_label\">Main Character</span>")

        private val GUILD_SEARCH_URL = "https://www.naeu.playblackdesert.com/en-US/Adventure/Guild?Page="
        private val GUILD_PAGE_URL = "https://www.naeu.playblackdesert.com/en-US/Adventure/Guild/GuildProfile?guildName="
        private val FAMILY_PAGE_URL = "https://www.naeu.playblackdesert.com/en-US/Adventure/Profile?profileTarget="
        private val FAMILY_SEARCH_URL = "https://www.naeu.playblackdesert.com/en-US/Adventure?searchType=2&region="

        private fun getGuildSearchUrl(page: Int, search: String) : String {
            return "$GUILD_SEARCH_URL${page}&searchText=$search"
        }

        private fun getGuildPageUrl(name: String, region: Region) : String {
            return "$GUILD_PAGE_URL$name&region=${region.code}"
        }

        private fun getFamilySearchUrl(name: String, region: Region) : String {
            return "$FAMILY_SEARCH_URL${region.code}&searchKeyword=$name"
        }

        private fun getFamilyPageUrl(id: String) : String {
            return "$FAMILY_PAGE_URL$id"
        }

        fun getGuildNamesOnPage(page: Int, search: String) : List<String> {
            val request = Request.Builder().url(getGuildSearchUrl(page, search)).build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected Http Code $response")

                val body = response.body!!.string()
                // regex body for guild names to return
                val names = GUILD_NAME_REGEX.findAll(body)
                return names.asStream().map { it.groupValues[2] }.collect(Collectors.toList())
            }
        }

        fun getGuildFamilies(guildName: String, region: Region) : Set<BdoFamily> {
            val request = Request.Builder().url(getGuildPageUrl(guildName, region)).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected Http Code $response")

                val body = response.body!!.string()
                // regex body for guild names to return
                val members = USER_PROFILE_REGEX.findAll(body)
                return members.asStream().map { BdoFamily(it.groupValues[2], it.groupValues[1], guildName) }
                        .collect(Collectors.toCollection(::HashSet))
            }
        }

        fun getUserInfoForSearch(familyName: String, region: Region) : Optional<BdoFamily> {
            val request = Request.Builder().url(getFamilySearchUrl(familyName, region)).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected http code $response")

                val body = response.body!!.string()
                val guild = GUILD_NAME_REGEX.find(body)
                // If member not found, empty opt else bdo family model
                val member = USER_PROFILE_REGEX.find(body) ?: return Optional.empty()
                val private = USER_PRIVATE_REGEX.containsMatchIn(body)
                val family = BdoFamily(member.groupValues[2], member.groupValues[1], guild?.groupValues?.get(2), private)
                // Populate character information if not private
//                if (!private) {
//                    family.characters = getCharactersForFamily(family.id)
//                }
                return Optional.of(family)
            }
        }

        private fun getCharactersForFamily(familyTarget: String) : List<BdoCharacter> {
            val request = Request.Builder().url(getFamilyPageUrl(familyTarget)).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected http code $response")

                val body = response.body!!.string()
                val characters = CHARACTER_INFO_REGEX.findAll(body)
                return characters.asStream().map { parseUserDescriptionHTML(it.groupValues[1]) }.collect(Collectors.toList())
            }
        }

        private fun parseUserDescriptionHTML(html: String) : BdoCharacter {
            val name = CHARACTER_NAME_REGEX.find(html)!!.groupValues[1]
            val main = CHARACTER_MAIN_REGEX.containsMatchIn(html)
            val classLevel = CHARACTER_CLASS_LEVEL_REGEX.findAll(html)
            val combatClass = classLevel.iterator().next().groupValues[1]
            val level = classLevel.iterator().next().groupValues[1].toInt()
            return BdoCharacter(name, combatClass, level, main)
        }
    }
}
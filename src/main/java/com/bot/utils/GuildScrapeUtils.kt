package com.bot.utils

import com.bot.models.BdoFamilyId
import com.bot.models.Region
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.stream.Collectors
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
        private val GUILD_MEMBER_REGEX = Regex("profileTarget=(.+)\">(.+)</a>")

        private val GUILD_SEARCH_URL = "https://www.naeu.playblackdesert.com/en-US/Adventure/Guild?searchText=&Page="
        private val GUILD_PAGE_URL = "https://www.naeu.playblackdesert.com/en-US/Adventure/Guild/GuildProfile?guildName="
        private val FAMILY_PAGE_URL = "https://www.naeu.playblackdesert.com/en-US/Adventure/Profile?profileTarget="

        private fun getGuildSearchUrl(page: Int) : String {
            return "$GUILD_SEARCH_URL$page"
        }

        private fun getGuildPageUrl(name: String, region: Region) : String {
            return "$GUILD_PAGE_URL$name&region=${region.code}"
        }

        private fun getFamilyPageUrl(id: String) : String {
            return "$FAMILY_PAGE_URL$id"
        }

        fun getGuildNamesOnPage(page: Int) : List<String> {
            val request = Request.Builder().url(getGuildSearchUrl(page)).build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected Http Code $response")

                val body = response.body!!.string()
                // regex body for guild names to return
                val names = GUILD_NAME_REGEX.findAll(body)
                return names.asStream().map { it.groupValues[2] }.collect(Collectors.toList())
            }
        }

        fun getGuildFamilies(guildName: String, region: Region) : Set<BdoFamilyId> {
            val request = Request.Builder().url(getGuildPageUrl(guildName, region)).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected Http Code $response")

                val body = response.body!!.string()
                // regex body for guild names to return
                val members = GUILD_MEMBER_REGEX.findAll(body)
                return members.asStream().map { BdoFamilyId(it.groupValues[2], it.groupValues[1]) }
                        .collect(Collectors.toCollection(::HashSet))
            }
        }
    }
}
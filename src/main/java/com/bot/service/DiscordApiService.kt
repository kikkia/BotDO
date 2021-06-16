package com.bot.service

import com.bot.configuration.properties.DiscordProperties
import com.bot.models.DiscordUserIdentity
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
open class DiscordApiService(val discordProperties: DiscordProperties) {
    private val DISCORD_TOKEN_ENDPOINT = "https://discord.com/api/v8/oauth2/token"

    private val client = OkHttpClient()

    open fun getUserToken(code: String) : DiscordUserIdentity {
        val requestBody = FormBody.Builder()
                .add("client_id", discordProperties.clientId)
                .add("client_secret", discordProperties.clientSecret)
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", "http://localhost:42069")
                .build()
        val request = Request.Builder()
                .post(requestBody)
                .url(DISCORD_TOKEN_ENDPOINT)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                print("Request to discord failed $response")
                throw RuntimeException("REEEEEE")
            }
            
        }
    }
}
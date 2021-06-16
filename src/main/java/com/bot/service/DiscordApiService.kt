package com.bot.service

import com.bot.configuration.properties.DiscordProperties
import com.bot.models.DiscordUserIdentity
import com.fasterxml.jackson.core.JsonParser
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
open class DiscordApiService(val discordProperties: DiscordProperties) {
    private val DISCORD_TOKEN_ENDPOINT = "https://discord.com/api/v8/oauth2/token"
    private val DISCORD_USER_ENDPOINT = "https://discord.com/api/v8/users/@me"

    private val client = OkHttpClient()

    open fun getUserAccessToken(code: String) : String {
        val requestBody = FormBody.Builder()
                .add("client_id", discordProperties.clientId)
                .add("client_secret", discordProperties.clientSecret)
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", "http://localhost:42069/auth/callback")
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
            val jsonRepsonse = JSONObject(response.body!!.string())
            return jsonRepsonse.getString("access_token")
        }
    }

    open fun getUserIdentity(accessToken: String) : DiscordUserIdentity {
        val request = Request.Builder()
                .url(DISCORD_USER_ENDPOINT)
                .addHeader("authorization", "Bearer $accessToken")
                .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                print("Request to discord failed $response")
                throw RuntimeException("REEEEEE")
            }
            val jsonRepsonse = JSONObject(response.body!!.string())
            return DiscordUserIdentity(jsonRepsonse.getString("id"),
                    jsonRepsonse.getString("username"),
                    jsonRepsonse.getInt("discriminator"))
        }
    }
}
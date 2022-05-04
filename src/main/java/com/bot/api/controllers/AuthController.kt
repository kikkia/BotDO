package com.bot.api.controllers

import com.bot.configuration.properties.APIProperties
import com.bot.models.DiscordUserIdentity
import com.bot.service.DiscordApiService
import com.bot.service.TokenService
import org.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

// This controller is responsible for getting a discord oauth2 callback and minting a signed jwt with user info
@RestController
@RequestMapping("/api/auth")
class AuthController(private val tokenService: TokenService,
                     private val discordApiService: DiscordApiService,
                     val apiProperties: APIProperties) {
    @RequestMapping("/callback")
    fun oauthCallback(@RequestParam code: String, response: HttpServletResponse) : ResponseEntity<String> {
        val discordToken = discordApiService.getUserAccessToken(code)
        val discordUser = discordApiService.getUserIdentity(discordToken)
        addAuthCookies(discordUser, response)
        return ResponseEntity.ok("success");
    }

    @CrossOrigin(origins = ["https://toshi.kikkia.dev"], allowCredentials = "true")
    @RequestMapping("/test")
    fun testAuth(@CookieValue(name = "token", defaultValue = "foo") token: String) : ResponseEntity<String> {
        return if (tokenService.validateToken(token)) {
            val name = tokenService.getSubjectFromToken(token)
            val avatar = tokenService.getStringClaimFromToken(token, "avatar")
            val response = JSONObject()
            response.put("name", name)
            response.put("avatar", avatar)
            ResponseEntity.ok(response.toString())
        } else {
            ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
    }

    @CrossOrigin(origins = ["https://toshi.kikkia.dev"], allowCredentials = "true")
    @RequestMapping("/logout")
    fun logout(response: HttpServletResponse) : ResponseEntity<String> {
        revokeAuthCookies(response)
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(apiProperties.frontendUrl)).build();
    }

    private fun addAuthCookies(user: DiscordUserIdentity, response: HttpServletResponse) {
        val cookies = mutableListOf<Cookie>()

        val tokenCookie = Cookie("token", tokenService.generateToken(user))
        tokenCookie.isHttpOnly = true

        val usernameCookie = Cookie("username", user.username)
        val userAvatarCookie = Cookie("avatar", user.avatar)
        val userIdCookie = Cookie("userId", user.userId)
        cookies.add(tokenCookie)
        cookies.add(usernameCookie)
        cookies.add(userAvatarCookie)
        cookies.add(userIdCookie)
        setAuthCookies(cookies, response, tokenService.JWT_TOKEN_VALIDITY.toInt())
    }

    private fun revokeAuthCookies(response: HttpServletResponse) {
        val cookies = mutableListOf<Cookie>()

        val tokenCookie = Cookie("token", null)
        tokenCookie.isHttpOnly = true

        val usernameCookie = Cookie("username", null)
        val userAvatarCookie = Cookie("avatar", null)
        val userIdCookie = Cookie("userId", null)
        cookies.add(tokenCookie)
        cookies.add(usernameCookie)
        cookies.add(userAvatarCookie)
        cookies.add(userIdCookie)
        setAuthCookies(cookies, response, 0)
    }

    private fun setAuthCookies(cookies: List<Cookie>, response: HttpServletResponse, maxAge: Int) {
        for (cookie in cookies) {
            cookie.maxAge = tokenService.JWT_TOKEN_VALIDITY.toInt()
            cookie.path = "/"
            response.addCookie(cookie)
        }
    }
}
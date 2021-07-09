package com.bot.controllers

import com.bot.configuration.properties.APIProperties
import com.bot.models.DiscordUserIdentity
import com.bot.service.DiscordApiService
import com.bot.service.TokenService
import org.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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
        setAuthCookies(discordUser, response)
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

    private fun setAuthCookies(user: DiscordUserIdentity, response: HttpServletResponse) {
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

        for (cookie in cookies) {
            cookie.maxAge = tokenService.JWT_TOKEN_VALIDITY.toInt()
            cookie.path = "/"
            response.addCookie(cookie)
        }
    }
}
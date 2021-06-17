package com.bot.controllers

import com.bot.configuration.properties.APIProperties
import com.bot.models.DiscordUserIdentity
import com.bot.service.DiscordApiService
import com.bot.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

// This controller is responsible for getting a discord oauth2 callback and minting a signed jwt with user info
@RestController
@RequestMapping("/auth")
class AuthController(private val tokenService: TokenService,
                     private val discordApiService: DiscordApiService,
                     val apiProperties: APIProperties) {
    @RequestMapping("/callback")
    fun oauthCallback(@RequestParam code: String, response: HttpServletResponse) : ResponseEntity<String> {
        val discordToken = discordApiService.getUserAccessToken(code)
        val discordUser = discordApiService.getUserIdentity(discordToken)
        setAuthCookies(discordUser, response)
        response.sendRedirect(apiProperties.frontendUrl)
        return ResponseEntity.ok("success");
    }

    @CrossOrigin(origins = ["http://toshi.kikkia.dev"], allowCredentials = "true")
    @RequestMapping("/test")
    fun testAuth(@CookieValue(name = "token", defaultValue = "foo") token: String) : ResponseEntity<String> {
        return if (tokenService.validateToken(token)) {
            ResponseEntity.ok("valid")
        } else {
            ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
    }

    private fun setAuthCookies(user: DiscordUserIdentity, response: HttpServletResponse) {
        val cookieDomain = apiProperties.domain
        val tokenCookie = Cookie("token", tokenService.generateToken(user))
        tokenCookie.maxAge = tokenService.JWT_TOKEN_VALIDITY.toInt()
        tokenCookie.isHttpOnly = true
        tokenCookie.domain = cookieDomain
        response.addCookie(tokenCookie)

        val displayNameCookie = Cookie("displayName", user.username)
        displayNameCookie.maxAge = tokenService.JWT_TOKEN_VALIDITY.toInt()
        displayNameCookie.domain = cookieDomain
        response.addCookie(displayNameCookie)

        val avatarCookie = Cookie("avatar", user.avatar)
        avatarCookie.maxAge = tokenService.JWT_TOKEN_VALIDITY.toInt()
        avatarCookie.domain = cookieDomain
        response.addCookie(avatarCookie)
    }
}
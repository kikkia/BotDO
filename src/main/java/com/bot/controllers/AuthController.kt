package com.bot.controllers

import com.bot.service.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

// This controller is responsible for getting a discord oauth2 callback and minting a signed jwt with user info
@RestController
@RequestMapping("/auth")
class AuthController(private val tokenService: TokenService) {

    @RequestMapping("/callback")
    fun oauthCallback(@RequestParam code: String) : String {
        val token = tokenService.generateToken("user")
        return "Code: $code \ntoken: $token"
    }
}
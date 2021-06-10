package com.bot.controllers

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

// This controller is responsible for getting a discord oauth2 callback and minting a signed jwt with user info
@RestController
@RequestMapping("/auth")
class AuthController {

    @RequestMapping("/callback")
    public fun oauthCallback(@RequestParam code: String, @RequestParam state: String) : String {
        return "Hello World"
    }
}
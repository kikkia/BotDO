package com.bot.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "api")
open class APIProperties {
    var domain = "localhost"
    var host = "http://localhost:42069"
    var frontendUrl = "http://localhost:3000";
    var requireAuth = true
    var keySecret = ""
}
package com.bot.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "db")
open class DatabaseProperties {
    var username = ""
    var password = ""
    var uri = ""
    var schema: String? = null
}
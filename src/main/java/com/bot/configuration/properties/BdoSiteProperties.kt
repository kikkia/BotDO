package com.bot.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "bdo")
open class BdoSiteProperties {
    var bdoSiteBaseUrl = listOf("https://www.naeu.playblackdesert.com/en-US/")
}
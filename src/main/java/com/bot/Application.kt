package com.bot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.boot.SpringApplication

@SpringBootApplication
@EnableConfigurationProperties
@ComponentScan
open class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}

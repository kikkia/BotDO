package com.bot.configuration.security

import com.bot.auth.AuthenticationFilter
import com.bot.service.TokenService
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.lang.Exception


@EnableWebSecurity
open class WebSecurityConfiguration(private val tokenService: TokenService) : WebSecurityConfigurerAdapter() {

    @Configuration
    @Order(1)
    open class ApiWebSecurityConfig(private val tokenService: TokenService) : WebSecurityConfigurerAdapter() {
        @Throws(Exception::class)
        override fun configure(http: HttpSecurity) {
            http.requestMatchers()
                    .antMatchers("/**")
                    .and()
                    .addFilterAfter(
                            AuthenticationFilter(tokenService), BasicAuthenticationFilter::class.java)
        }
    }
}
package com.bot.auth

import com.bot.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import javax.servlet.ServletException
import javax.servlet.FilterChain

import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
import java.util.*
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

// Filter to just ensure that the jwt on the request is valid
class AuthenticationFilter(val tokenService: TokenService) : GenericFilterBean() {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(
            request: ServletRequest,
            response: ServletResponse,
            chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        // If auth callback, do not check
        if (httpRequest.requestURI == "/auth/callback") {
            chain.doFilter(request, response)
            return
        }

        val cookie = getTokenCookie(httpRequest)
        if (cookie.isEmpty) {
            // No token cookie, 401
            httpResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "Missing Token cookie")
            return
        }
        val token = cookie.get().value
        if (!tokenService.validateToken(token)) {
            // Invalid token, 401
            httpResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid Token")
            return
        }

        SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(tokenService.getStringClaimFromToken(token, "userId"), token, null)

        chain.doFilter(request, response)
    }

    private fun getTokenCookie(req: HttpServletRequest) : Optional<Cookie> {
        if (req.cookies == null) {
            return Optional.empty()
        }

        for (cookie in req.cookies) {
            if (cookie.name == "token") {
                return Optional.of(cookie)
            }
        }
        return Optional.empty()
    }
}
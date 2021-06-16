package com.bot.service

import org.springframework.stereotype.Service
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.time.Instant
import java.util.*
import kotlin.collections.HashMap

@Service
class TokenService {
    private val serialVersionUID = -255018516526007488L
    // TODO: Set this up for private key
    private val key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    val JWT_TOKEN_VALIDITY = (12 * 60 * 60).toLong()

    //retrieve username from jwt token
    fun getUsernameFromToken(token: String?): String {
        val claims: Claims = getAllClaimsFromToken(token)
        return claims.subject
    }

    //retrieve expiration date from jwt token
    fun getExpirationDateFromToken(token: String?): Date {
        val claims: Claims = getAllClaimsFromToken(token)
        return claims.expiration
    }

    //for retrieving any information from token we will need the secret key
    private fun getAllClaimsFromToken(token: String?): Claims {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
    }

    //check if the token has expired
    private fun isTokenExpired(token: String?): Boolean? {
        val expiration: Date = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    //generate token for user
    fun generateToken(username: String): String? {
        val claims = HashMap<String, Any>()
        claims["test1"] = "test1"
        claims["test2"] = "test2"
        return doGenerateToken(claims, username)
    }

    private fun doGenerateToken(claims: Map<String, Any>, subject: String): String? {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setExpiration(
                        Date.from(Instant.now().plusSeconds(JWT_TOKEN_VALIDITY)))
                .signWith(key)
                .compact();
    }

    //validate token
    fun validateToken(token: String?, username: String): Boolean {
        val tokenUsername = getUsernameFromToken(token)
        return tokenUsername == username && !isTokenExpired(token)!!
    }
}
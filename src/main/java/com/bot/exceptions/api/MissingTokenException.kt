package com.bot.exceptions.api

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class MissingTokenException(message: String): RuntimeException(message) {
}
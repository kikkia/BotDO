package com.bot.api.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
class WarsNotSetupException(message: String) : RuntimeException(message) {
}
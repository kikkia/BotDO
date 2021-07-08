package com.bot.exceptions.api

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(HttpStatus.NOT_FOUND)
class GuildNotFoundException(message: String) : RuntimeException(message)
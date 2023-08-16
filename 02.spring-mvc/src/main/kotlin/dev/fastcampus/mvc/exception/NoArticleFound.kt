package dev.fastcampus.mvc.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(HttpStatus.NOT_FOUND)
class NoArticleFound(message: String?): RuntimeException(message)
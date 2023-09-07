package dev.fastcampus.payment.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException: RuntimeException {
    constructor(): super()
    constructor(message: String?): super(message)
}
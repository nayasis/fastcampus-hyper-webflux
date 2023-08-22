package dev.fastcampus.payment.model.parent

import au.com.console.kassava.kotlinToString
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.io.Serializable
import java.time.LocalDateTime

open class BaseEntity (
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
): Serializable {
    override fun toString(): String =
        kotlinToString(arrayOf(BaseEntity::createdAt, BaseEntity::updatedAt))
}
package dev.fastcampus.coroutine.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.LocalDateTime
import javax.annotation.processing.Generated

@Table("TB_ARTICLE")
class Article (

    @Id
    @Generated
    var id: Long = 0,

    var title: String = "",

    var body: String? = null,

    var authorId: Long? = null,

    @Version
    var version: Int = 0,

): BaseEntity(), Serializable {

    override fun equals(other: Any?): Boolean =
        kotlinEquals(other, arrayOf(Article::id))

    override fun hashCode(): Int =
        kotlinHashCode(arrayOf(Article::id))

    override fun toString(): String =
        kotlinToString(
            arrayOf(Article::id, Article::title, Article::body, Article::authorId, Article::version),
            superToString = { super.toString() }
        )

}

open class BaseEntity (
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
): Serializable {
    override fun toString(): String =
        kotlinToString(arrayOf(BaseEntity::createdAt, BaseEntity::updatedAt))
}
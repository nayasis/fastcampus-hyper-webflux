package dev.fastcampus.springmvc.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity(name="TB_ARTICLE")
@EntityListeners(AuditingEntityListener::class)
class Article(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var title: String,
    var body: String? = null,
    var authorId: Long? = null,
): BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Article
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String {
        return "Article(id=$id, title='$title', body='$body', authorId=$authorId, ${super.toString()})"
    }
}

@MappedSuperclass
open class BaseEntity(
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
) {
    override fun toString(): String {
        return "createdAt=$createdAt, updatedAt=$updatedAt"
    }
}
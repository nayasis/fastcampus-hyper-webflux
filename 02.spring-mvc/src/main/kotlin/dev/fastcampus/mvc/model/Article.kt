package dev.fastcampus.mvc.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.*
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.LocalDateTime

@Entity(name = "TB_ARTICLE")
@EntityListeners(AuditingEntityListener::class)
class Article(

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name="id")
    var id: Long = 0,

    @Column(name="title")
    var title: String = "",

    @Column(name="body")
    var body: String? = null,

    @Column(name="author_id")
    var authorId: Long? = null,

): BaseEntity(), Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Article
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String =
        "Article(id=$id, title=$title, body=$body, authorId=$authorId, ${super.toString()})"

}

@MappedSuperclass
open class BaseEntity (
    @CreatedDate
    @Column(name="created_at")
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name="updated_at")
    var updatedAt: LocalDateTime? = null,
): Serializable {
    override fun toString(): String = "createdAt=$createdAt, updatedAt=$updatedAt"
}
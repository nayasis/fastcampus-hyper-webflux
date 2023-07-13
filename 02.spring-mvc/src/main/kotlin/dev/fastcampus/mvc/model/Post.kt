package dev.fastcampus.mvc.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.*
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity(name = "TB_POST")
@EntityListeners(AuditingEntityListener::class)
class Post {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long = 0
    var title: String? = null
    var body: String? = null
    var authorId: Long? = null
    @CreatedDate
    var createdAt: LocalDateTime? = null
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null

}
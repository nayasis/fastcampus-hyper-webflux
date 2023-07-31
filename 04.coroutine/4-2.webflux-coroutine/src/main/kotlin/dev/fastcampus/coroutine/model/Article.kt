package dev.fastcampus.coroutine.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import javax.annotation.processing.Generated

@Table("TB_ARTICLE")
class Article {

    @Id
    @Generated
    var id: Long = 0
    var title: String? = null
    var body: String? = null
    var authorId: Long? = null
    @CreatedDate
    var createdAt: LocalDateTime? = null
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null

    constructor()
    constructor(id: Long, title: String, body: String, authorId: Long) {
        this.id = id
        this.title = title
        this.body = body
        this.authorId = authorId
    }

}
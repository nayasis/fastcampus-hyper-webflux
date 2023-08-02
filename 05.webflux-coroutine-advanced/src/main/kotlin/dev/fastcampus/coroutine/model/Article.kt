package dev.fastcampus.coroutine.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.LocalDateTime
import javax.annotation.processing.Generated

@Table("TB_ARTICLE")
class Article: Serializable {

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

    @Version
    var version: Int = 0

}
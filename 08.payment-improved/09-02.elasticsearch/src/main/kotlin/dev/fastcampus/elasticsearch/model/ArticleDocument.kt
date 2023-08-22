package dev.fastcampus.elasticsearch.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDateTime

@Document(indexName = "article")
class ArticleDocument(
    @Id
    var id: Long = 0,

    var title: String? = null,

    var body: String? = null,

    var authorId: Long? = null,

    @Field(type = FieldType.Date, format = [DateFormat.date_hour_minute_second_millis])
    var createdAt: LocalDateTime? = LocalDateTime.now(),

    @Field(type = FieldType.Date, format = [DateFormat.date_hour_minute_second_millis])
    var updatedAt: LocalDateTime? = LocalDateTime.now(),

)
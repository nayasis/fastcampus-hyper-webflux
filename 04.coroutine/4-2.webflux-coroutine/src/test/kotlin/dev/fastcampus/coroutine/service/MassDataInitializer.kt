package dev.fastcampus.coroutine.service

import dev.fastcampus.coroutine.model.Article
import io.kotest.core.spec.style.StringSpec
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class MassDataInitializer: StringSpec({

    "mass insert".config(enabled = false) {
        connectDb()
        transaction {
            Articles.deleteAll()
            val buffer = ArrayList<Article>()
            repeat(1_999_999) { i ->
//            repeat(100_000) { i ->
                buffer.add(Article(title = "title $i", body="body $i", authorId=i.toLong() ))
                if( i % 20000 == 0) {
                    println(">> $i")
                    Articles.batchInsert(
                        buffer,
                        ignore = false,
                    ){
                        this[Articles.title] = it.title!!
                        this[Articles.body] = it.body!!
                        this[Articles.authorId] = it.authorId!!
                        this[Articles.createdAt] = DateTime.now()
                        this[Articles.updatedAt] = DateTime.now()
                    }
                    commit()
                    buffer.clear()
                }
            }

            buffer.add(
                Article(title="title matched", body="contents matched", authorId=0L )
            )
            Articles.batchInsert(
                buffer,
                ignore = false,
            ){
                this[Articles.title] = it.title!!
                this[Articles.body] = it.body!!
                this[Articles.authorId] = it.authorId!!
                this[Articles.createdAt] = DateTime.now()
                this[Articles.updatedAt] = DateTime.now()
            }
            commit()
        }
    }


    "sample".config(enabled = false) {
        connectDb()
        transaction {
            addLogger(StdOutSqlLogger)

            ArticleDao
                .find {
                    (Articles.title like "% 10%").and(
                        Articles.body like "%45"
                    )
                }.forEach { println(it) }

            val rs = Articles.selectAll().apply {
                andWhere { Articles.title like "% 10%" }
                andWhere { Articles.body like "%45" }
            }
            println("count : ${rs.count()}")
            println(rs.joinToString("\n"))
        }
    }
})

private fun connectDb() {
    Database.connect(
        url = "jdbc:mariadb://localhost:3306/sample?rewriteBatchedStatements=true",
        driver = "org.mariadb.jdbc.Driver",
        user = "user",
        password = "1234"
    )
}

object Articles: LongIdTable("TB_ARTICLE") {
    val title = varchar("title", 255)
    val body = varchar("body", 255)
    val authorId = long("author_id")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}

class ArticleDao(id: EntityID<Long>): LongEntity(id) {
    companion object: LongEntityClass<ArticleDao>(Articles)
    var title by Articles.title
    var body by Articles.body
    var authorId by Articles.authorId
    var createdAt by Articles.createdAt
    var updatedAt by Articles.updatedAt

    override fun toString(): String {
        return "ArticleDao(title='$title', body='$body', authorId=$authorId, createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}
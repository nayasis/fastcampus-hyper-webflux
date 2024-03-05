package dev.fastcampus

import dev.fastcampus.Articles.authorId
import dev.fastcampus.Articles.body
import dev.fastcampus.Articles.createdAt
import dev.fastcampus.Articles.title
import dev.fastcampus.Articles.updatedAt
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.mariadb.jdbc.Driver

fun insertByArticles() {
    connectDb()
    transaction {
        addLogger(StdOutSqlLogger)
        Articles.insert {
            it[title]     = "title 1"
            it[body]      = "body 1"
            it[authorId]  = 1234
            it[createdAt] = DateTime.now()
            it[updatedAt] = DateTime.now()
        }
        Articles.select { title like "%2%" }.forEach { row ->
            println(row)
        }
        Articles.selectAll().forEach { row ->
            println(row)
        }
    }
}

fun insertByArticle() {
    connectDb()
    transaction {
        addLogger(StdOutSqlLogger)

        Article.new {
            title     = "title 1"
            body      = "body 1"
            authorId  = 1234
            createdAt = DateTime.now()
            updatedAt = DateTime.now()
        }

        Article.all().forEach {
            println(it)
        }
        Article.find { title like "%2%" }.forEach { println(it) }
    }
}


fun batchInsert() {

    val bufferSize = 10000
    val dataSize   = 1_999_999L

    connectDb()
    transaction {
        Articles.deleteAll()
        (1L..dataSize).windowed(size = bufferSize, step = bufferSize, partialWindows = true).forEachIndexed { index, nums ->
            Articles.batchInsert(nums, false) {
                this[title]     = "title $it"
                this[body]      = "body $it"
                this[authorId]  = it
                this[createdAt] = DateTime.now()
                this[updatedAt] = DateTime.now()
            }
            commit()
            println(">> inserted: ${"%3d".format(index + 1)} x $bufferSize")
        }

        Article.new {
            title     = "title matched"
            body      = "body matched"
            authorId  = -1
            createdAt = DateTime.now()
            updatedAt = DateTime.now()
        }
        commit()
    }
}

fun main() {
    batchInsert()
}


object Articles: LongIdTable("TB_ARTICLE") {
    val title     = varchar("title", 255)
    val body      = varchar("body", 2000)
    val authorId  = long("author_id")
    val createdAt = datetime("created_at").nullable()
    val updatedAt = datetime("updated_at").nullable()
}

class Article(id: EntityID<Long>): LongEntity(id) {
    companion object: LongEntityClass<Article>(Articles)

    var title     by Articles.title
    var body      by Articles.body
    var authorId  by Articles.authorId
    var createdAt by Articles.createdAt
    var updatedAt by Articles.updatedAt

    override fun toString(): String =
        "Article(title='$title', body='$body', authorId=$authorId, createdAt=$createdAt, updatedAt=$updatedAt)"
}

private fun connectDb() {
    Database.connect(
        url      = "jdbc:mariadb://localhost:3306/sample?rewriteBatchedStatements = true",
        driver   = Driver::class.qualifiedName!!,
        user     = "user",
        password = "1234"
    )
}
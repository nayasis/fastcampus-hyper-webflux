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

//@SpringBootTest
//class MassDataInitializer(
//    @Autowired private val repository: ArticleRepository,
//    @Autowired private val connectionFactory: ConnectionFactory,
//): StringSpec({
//
//    "mass initialize".config(enabled = true) {
//
//        repository.deleteAll()
//
//        val connection = connectionFactory.create().awaitSingle()
//        var batch = connection.createBatch()
//
//        repeat(199_999_999) { i ->
////        repeat(10000) { i ->
//            batch.add("""
//                INSERT INTO TB_ARTICLE(
//                    title, body, author_id, created_at, updated_at
//                ) VALUES (
//                    'title $i',
//                    'body $i',
//                    $i,
//                    current_timestamp(),
//                    current_timestamp()
//                )
//            """.trimIndent())
//            if( i % 10000 == 0 ) {
//                println("$i")
////                executer.execute()
//                Flux.from(batch.execute()).subscribeOn(Schedulers.boundedElastic()).blockLast()
//                batch = connection.createBatch()
//
////                Flux.from(executer.execute()).subscribeOn(Schedulers.boundedElastic()).last().awaitSingle()
////                batch.execute().toMono().subscribeOn(Schedulers.boundedElastic()).awaitSingle()
//            }
//        }
//
//        batch.add("""
//                INSERT INTO TB_ARTICLE(
//                    title, body, author_id, created_at, updated_at
//                ) VALUES (
//                    'search',
//                    'body',
//                    0,
//                    current_timestamp(),
//                    current_timestamp()
//                )
//            """.trimIndent())
//
////        Flux.from(executer.execute()).subscribeOn(Schedulers.boundedElastic()).blockLast()
//        Flux.from(batch.execute()).subscribeOn(Schedulers.boundedElastic()).last().awaitSingle()
////        batch.execute().toMono().subscribeOn(Schedulers.boundedElastic()).awaitSingle()
//
//        connection.close()
//
//    }
//
//})

class MassDataInitializer: StringSpec({

    "mass insert".config(enabled = true) {
        connectDb()
        transaction {
            Articles.deleteAll()
            val buffer = ArrayList<Article>()
            repeat(9_999_999) { i ->
                buffer.add(Article("title $i", "body $i", i.toLong() ))
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
                Article("search", "match", 0.toLong() )
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
                    (Articles.title like "% 2002%").and(
                        Articles.body like "%45"
                    )
                }.forEach { println(it) }

            val rs = Articles.selectAll().apply {
                andWhere { Articles.title like "% 2002%" }
                andWhere { Articles.body like "%45" }
            }.forEach { println(it) }
//            println("count : ${rs.count()}")
//            println(rs.joinToString("\n"))
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
package dev.fastcampus.coroutine.service

import dev.fastcampus.coroutine.model.Article
import dev.fastcampus.coroutine.repository.ArticleRepository
import io.kotest.core.spec.style.StringSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MassDataInitilizer(
    @Autowired private val repository: ArticleRepository,
): StringSpec({

    "mass initialize".config(enabled = true) {
        repository.deleteAll()
        val buffer = ArrayList<Article>()
        repeat(Int.MAX_VALUE - 1) {i ->
            println("$i")
            if( i % 10000 == 0 && buffer.size > 0) {
                repository.saveAll(buffer)
                buffer.clear()
            } else {
                buffer.add(Article(
                    id = i.toLong(),
                    title = "title $i",
                    body = "body $i",
                    authorId = i.toLong()
                ))
            }
        }
        buffer.add(Article(
            id = Int.MAX_VALUE.toLong(),
            title = "search",
            body = "body",
            authorId = 0.toLong()
        ))
        repository.saveAll(buffer)
    }

})
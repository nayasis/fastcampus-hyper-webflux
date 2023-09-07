package dev.fastcampus.elasticsearch

import dev.fastcampus.elasticsearch.common.WithTestContainer
import dev.fastcampus.elasticsearch.model.Article
import dev.fastcampus.elasticsearch.repository.ArticleRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {}

@SpringBootTest
class ElasticsearchApplicationTests(
	@Autowired repository: ArticleRepository
): WithTestContainer, StringSpec({

	"context loads" {

		repository.save(Article(1, "title-1", "blabla-1", 1234))
		repository.save(Article(2, "title-2", "blabla-2", 5678))
		repository.save(Article(3, "title-3", "blabla-3", 1234))
		repository.save(Article(4, "title-4", "blabla-4", 5678))
		delay(1.seconds)
		repository.count() shouldBe 4

		repository.deleteById(1)
		delay(1.seconds)
		repository.count() shouldBe 3

	}

})
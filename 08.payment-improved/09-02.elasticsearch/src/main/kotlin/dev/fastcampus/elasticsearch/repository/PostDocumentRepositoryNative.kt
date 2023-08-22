package dev.fastcampus.elasticsearch.repository

import dev.fastcampus.elasticsearch.model.ArticleDocument
import kotlinx.coroutines.reactor.awaitSingle
import mu.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.domain.Sort.by
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.stereotype.Component
import java.io.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ofPattern
import kotlin.reflect.KProperty

private val logger = KotlinLogging.logger {}


@Component
class PostDocumentRepositoryNative(
    private val elasticsearchTemplate: ReactiveElasticsearchTemplate,
) {

    suspend fun search(request: QrySearch): ResSearch {

        logger.debug { ">> request : $request" }

        val criteria = Criteria().apply {
            request.title?.split(" ")?.forEach {
                and(ArticleDocument::title.criteria.contains(it))
            }
            request.body?.split(" ")?.forEach {
                and(ArticleDocument::body.criteria.contains(it))
            }
            request.authorId?.let {
                and(ArticleDocument::authorId.criteria.`in`(it))
            }
            request.from?.let {
                LocalDate.parse(it, ofPattern("yyyy-MM-dd")).atStartOfDay()
            }?.let {
                and(ArticleDocument::createdAt.criteria.greaterThanEqual(it))
            }
            request.to?.let {
                LocalDate.parse(it, ofPattern("yyyy-MM-dd")).plusDays(1).atStartOfDay().minusNanos(1)
            }?.let {
                and(ArticleDocument::createdAt.criteria.lessThanEqual(it))
            }
        }

        val query = CriteriaQuery(criteria, request.pageable).apply {
            sort = ArticleDocument::createdAt.sort(Direction.DESC).and(ArticleDocument::id.sort(Direction.DESC))
            searchAfter = request.nextKey
        }

        val page = elasticsearchTemplate.searchForPage(query, ArticleDocument::class.java).awaitSingle()

        return ResSearch(
            page.content.map { it.content },
            page.totalElements,
            page.content.lastOrNull()?.sortValues,
        )

    }

}

val KProperty<*>.criteria: Criteria
    get() = Criteria(this.name)

fun KProperty<*>.sort(direction: Direction = Direction.ASC): Sort = by(direction, this.name)


data class QrySearch(
    var title: String? = null,
    var body: String? = null,
    var authorId: List<Long>? = null,
    var from: String? = null,
    var to: String? = null,
    var size: Int = 20,
    var nextKey: List<Any>? = null
): Serializable {
    val pageable: Pageable
        get() = PageRequest.of(0, size)

}

data class ResSearch(
    val posts: List<ArticleDocument>,
    val total: Long,
    val nextKey: List<Any>?,
)
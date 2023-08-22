package dev.fastcampus.elasticsearch.repository

import dev.fastcampus.elasticsearch.common.criteria
import dev.fastcampus.elasticsearch.common.fields
import dev.fastcampus.elasticsearch.common.sort
import dev.fastcampus.elasticsearch.common.toLocalDateTime
import dev.fastcampus.elasticsearch.model.Article
import kotlinx.coroutines.reactor.awaitSingle
import mu.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.stereotype.Component
import java.io.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ofPattern

private val logger = KotlinLogging.logger {}


@Component
class PostDocumentRepositoryNative(
    private val template: ReactiveElasticsearchTemplate,
) {

    suspend fun search(request: QrySearch): ResSearch {

        logger.debug { ">> request : $request" }

        val criteria = Criteria().apply {
            request.title?.split(" ")?.forEach {
                and(Article::title.criteria.contains(it))
            }
            request.body?.split(" ")?.forEach {
                and(Article::body.criteria.contains(it))
            }
            request.authorId?.let {
                and(Article::authorId.criteria.`in`(it))
            }
            request.from?.let {it.toLocalDateTime() }?.let {
                and(Article::createdAt.criteria.greaterThanEqual(it))
            }
            request.to?.let {it.toLocalDateTime().plusDays(1).minusNanos(1) }?.let {
                and(Article::createdAt.criteria.lessThanEqual(it))
            }
        }

        val query = CriteriaQuery(criteria, request.pageable).apply {
            sort = Article::createdAt.sort(Direction.DESC).and(Article::id.sort(Direction.DESC))
            searchAfter = request.nextKey
        }

        val page = template.searchForPage(query, Article::class.java).awaitSingle()

        return ResSearch(
            page.content.map { it.content },
            page.totalElements,
            page.content.lastOrNull()?.sortValues,
        )

    }

    suspend fun searchNative(request: QrySearchV2): ResSearch {

        val query = NativeQuery.builder().withQuery{ it.bool{ it.apply {

            request.keyword?.trim()?.split(" ")
                ?.let { it.toSet().map { "*$it*" }.joinToString(" ") }
                ?.let { value ->
                    must{ it.queryString {
                        it.fields(Article::title, Article::body).query(value).analyzeWildcard(true)
                    }}
                }
            request.authorId?.let {
                must{ it.

                }
            }

        }}}

    }

}

data class QrySearchV2(
    val keyword: String? = null,
    val authorId: Set<Long>? = null,
    val from: String? = null,
    val to: String? = null,
    val size: Int = 20,
    val nextKey: List<Any>? = null
): Serializable {
    val pageable: Pageable
        get() = PageRequest.of(0, size)
}

data class QrySearch(
    val title: String? = null,
    val body: String? = null,
    val authorId: List<Long>? = null,
    val from: String? = null,
    val to: String? = null,
    val size: Int = 20,
    val nextKey: List<Any>? = null
): Serializable {
    val pageable: Pageable
        get() = PageRequest.of(0, size)
}

data class ResSearch(
    val posts: List<Article>,
    val total: Long,
    val nextKey: List<Any>?,
)
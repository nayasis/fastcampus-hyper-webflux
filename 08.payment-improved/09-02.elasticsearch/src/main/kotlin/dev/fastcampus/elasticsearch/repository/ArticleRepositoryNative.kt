package dev.fastcampus.elasticsearch.repository

import co.elastic.clients.elasticsearch._types.SortOrder
import dev.fastcampus.elasticsearch.common.criteria
import dev.fastcampus.elasticsearch.common.field
import dev.fastcampus.elasticsearch.common.fields
import dev.fastcampus.elasticsearch.common.gte
import dev.fastcampus.elasticsearch.common.lt
import dev.fastcampus.elasticsearch.common.sort
import dev.fastcampus.elasticsearch.common.toLocalDateTime
import dev.fastcampus.elasticsearch.common.values
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

private val logger = KotlinLogging.logger {}


@Component
class PostDocumentRepositoryNative(
    private val template: ReactiveElasticsearchTemplate,
) {

    suspend fun search(request: QrySearch): ResSearch {
        logger.debug { ">> request : $request" }
        val criteria = Criteria().apply {
//            request.title?.split(" ")?.forEach {
//                and(Article::title.criteria.contains(it))
//            }
//            request.body?.split(" ")?.forEach {
//                and(Article::body.criteria.contains(it))
//            }
            request.keyword?.split(" ")?.forEach {
                and(
                    or(Article::title.criteria.contains(it)),
                    or(Article::body.criteria.contains(it)),
                )
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
        return template.searchForPage(query, Article::class.java).awaitSingle().let { page ->
            ResSearch(
                page.content.map { it.content },
                page.totalElements,
                page.content.lastOrNull()?.sortValues,
            )
        }
    }

    suspend fun searchByNativeQuery(request: QrySearch): ResSearch {
        val query = NativeQuery.builder().withQuery{ it.bool{ it.apply {
            request.keyword?.trim()?.split(" ")
                ?.let { it.toSet().map { "*$it*" }.joinToString(" ") }
                ?.let { value ->
                    must{ it.queryString {
                        it.fields(Article::title, Article::body).query(value).analyzeWildcard(true)
                    }}
                }
            request.authorId?.ifEmpty{null}?.let {value ->
                filter{
                    it.terms {
//                        it.field(Article::authorId).terms { it.value(value.map { FieldValue.of("$it") }) }
                        it.field(Article::authorId).values(value)
                    }
                }
            }
            request.from?.let { it.toLocalDateTime() }?.let { value ->
                must {
                    it.range { it.field(Article::createdAt).gte(value) }
                }
            }
            request.to?.let { it.toLocalDateTime().plusDays(1) }?.let { value ->
                must {
                    it.range { it.field(Article::createdAt).lt(value) }
                }
            }
        }}}
            .withPageable(request.pageable)
            .withSearchAfter(request.nextKey)
            .withSort {
                it.field {
                    it.field(Article::createdAt).order(SortOrder.Desc)
                }
            }.build()
        return template.searchForPage(query, Article::class.java).awaitSingle().let { rs ->
            ResSearch(
                rs.content.map { it.content },
                rs.totalElements,
                rs.content.lastOrNull()?.sortValues,
            )
        }
    }

}

data class QrySearch(
    val title: String? = null,
    val body: String? = null,
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

data class ResSearch(
    val posts: List<Article>,
    val total: Long,
    val nextKey: List<Any>?,
)
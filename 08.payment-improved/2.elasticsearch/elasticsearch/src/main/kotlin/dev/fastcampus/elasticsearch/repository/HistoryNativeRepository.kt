package dev.fastcampus.elasticsearch.repository

import dev.fastcampus.elasticsearch.config.extension.toLocalDate
import dev.fastcampus.elasticsearch.model.History
import dev.fastcampus.elasticsearch.model.Status
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.stereotype.Service
import kotlin.reflect.KProperty

@Service
class HistoryNativeRepository(
    private val template: ReactiveElasticsearchTemplate,
) {
    suspend fun search(request: QrySearch): ResSearch<History> {

        val criteria = Criteria().apply {
            request.orderId?.let { and(
//                Criteria(History::orderId.name).`in`(it)
                History::orderId.criteria.`in`(it)
            )}
            request.userId?.let { and(
                History::userId.criteria.`in`(it)
            )}
            request.keyword?.split(" ")?.forEach { and(
                History::description.criteria.contains(it)
            )}
            request.status?.let { and(
                History::status.criteria.`in`(it)
            )}
            request.fromDt?.toLocalDate()?.atStartOfDay()?.let { and(
                History::createdAt.criteria.greaterThanEqual(it)
            )}
            request.toDt?.toLocalDate()?.plusDays(1)?.atStartOfDay()?.let { and (
                History::createdAt.criteria.lessThan(it)
            )}
            request.fromAmount?.let { and(
                History::amount.criteria.greaterThanEqual(it)
            )}
            request.toAmount?.let { and (
                History::amount.criteria.lessThanEqual(it)
            )}
        }

        val query = CriteriaQuery(criteria, request.pageable).apply {
//            sort = Sort.by(Direction.DESC,History::createdAt.name)
            sort = History::createdAt.sort(Direction.DESC)
            searchAfter = request.pageNext as? List<Long>
        }

        return template.searchForPage(query, History::class.java).awaitSingle().toResSearch()
//            .let { res ->
//                res.totalElements
//                res.content.map { it.content }
//                res.content.lastOrNull()?.sortValues
//            }
    }
}

val KProperty<*>.criteria: Criteria
    get() {
        return Criteria(this.name)
    }

fun KProperty<*>.sort(direction: Direction = Direction.ASC): Sort {
    return Sort.by(direction, this.name)
}

data class QrySearch(
    val orderId: List<Long>?,
    val userId: List<Long>?,
    val keyword: String?,
    val status: List<Status>?,
    val fromDt: String?,
    val toDt: String?,
    val fromAmount: Long?,
    val toAmount: Long?,
    val pageSize: Int = 10,
    val pageNext: List<Long>? = null,
) {
    val pageable: Pageable
        get() = PageRequest.of(0, pageSize)
}


data class ResSearch<T>(
    val items: List<T>,
    val totalSize: Long,
    val pageNext: List<Any>?,
)

fun <T> SearchPage<T>.toResSearch(): ResSearch<T> {
    return this.let { ResSearch(
        items = it.content.map { it.content },
        totalSize = it.totalElements,
        pageNext = it.content.lastOrNull()?.sortValues
    )}
}
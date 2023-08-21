package dev.fastcampus.payment.service

import dev.fastcampus.payment.model.PurchaseHistory
import dev.fastcampus.payment.model.code.TxStatus
import dev.fastcampus.payment.repository.PurchaseHistoryRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.stereotype.Service
import kotlin.reflect.KProperty

@Service
class PurchaseService(
    private val purchaseHistoryRepository: PurchaseHistoryRepository,
    private val orderService: OrderService,
    private val esTemplate: ReactiveElasticsearchTemplate,
) {

    suspend fun save(orderId: Long): PurchaseHistory {
        return orderService.get(orderId)
            .let { PurchaseHistory().fromOrder(it) }
            .let { purchaseHistoryRepository.save(it) }
    }

    suspend fun search(request: QrySearch): ResSearch {

        val criteria = Criteria().apply {
            and(PurchaseHistory::userId.criteria.matches(request.userId))
            and(PurchaseHistory::status.criteria.matchesAll(listOf(
                TxStatus.REQUEST_CONFIRM,
                TxStatus.SUCCESS,
                TxStatus.FAIL,
                TxStatus.NEED_CHECK,
            )))
            request.keyword?.trim()?.split(" ")?.forEach {
                and(
                    PurchaseHistory::prodNm.criteria.contains(it).or(
                        PurchaseHistory::description.criteria.contains(it)
                    )
                )
            }
            request.fromDate?.let { it.toDate().atStartOfDay() }?.let {
                and(PurchaseHistory::createdAt.criteria.greaterThanEqual(it))
            }
            request.toDate?.let { it.toDate().atStartOfDay() }?.let {
                and(PurchaseHistory::updatedAt.criteria.lessThanEqual(it))
            }
            request.fromAmount?.let {
                and(PurchaseHistory::amount.criteria.greaterThanEqual(it))
            }
            request.toAmount?.let {
                and(PurchaseHistory::amount.criteria.greaterThanEqual(it))
            }
        }

        val query = CriteriaQuery(criteria, request.pageable).apply {
            sort = PurchaseHistory::createdAt.sort(Sort.Direction.DESC)
            searchAfter = request.nextKey
        }

        val rs = esTemplate.searchForPage(query, PurchaseHistory::class.java).awaitSingle()

        return ResSearch(
            rs.content.map { it.content },
            rs.totalElements,
            rs.content.lastOrNull()?.sortValues,
        )

    }


}

val KProperty<*>.criteria: Criteria
    get() = Criteria(this.name)

fun KProperty<*>.sort(direction: Sort.Direction = Sort.Direction.ASC): Sort = Sort.by(direction, this.name)

data class QrySearch(
    val userId: Long,
    val keyword: String?,
    val fromDate: String?,
    val toDate: String?,
    val fromAmount: Long?,
    val toAmount: Long?,

    var size: Int = 20,
    var nextKey: List<Any>? = null
) {
    val pageable: Pageable
        get() = PageRequest.of(0, size)
}

data class ResSearch(
    val histories: List<PurchaseHistory>,
    val total: Long,
    val nextKey: List<Any>?,
)
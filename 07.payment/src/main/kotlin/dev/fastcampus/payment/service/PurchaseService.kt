package dev.fastcampus.payment.service

import co.elastic.clients.elasticsearch._types.FieldSort
import co.elastic.clients.elasticsearch._types.SortOrder
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery
import co.elastic.clients.json.JsonData
import com.fasterxml.jackson.databind.ObjectMapper
import dev.fastcampus.payment.model.Order
import dev.fastcampus.payment.model.PurchaseHistory
import dev.fastcampus.payment.model.code.TxStatus
import kotlinx.coroutines.reactor.awaitSingle
import mu.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KProperty


private val logger = KotlinLogging.logger {}

@Service
class PurchaseService(
    private val esTemplate: ReactiveElasticsearchTemplate,
    private val kafkaService: KafkaService,
    private val objectMapper: ObjectMapper,
) {

//    suspend fun save(order: Order): PurchaseHistory {
//        return PurchaseHistory().fromOrder(order)
//            .let { purchaseHistoryRepository.save(it) }
//    }

    suspend fun save(order: Order): PurchaseHistory {
        return PurchaseHistory().fromOrder(order)
            .also { kafkaService.send(it) }
    }

    suspend fun search(request: QrySearch): ResSearch {
        val nativeQuery = NativeQuery.builder().withQuery { it.bool { it.apply {
            must {
                it.queryString {
                    it.fields(PurchaseHistory::status).query(listOf(
                        TxStatus.REQUEST_CONFIRM,
                        TxStatus.SUCCESS,
                        TxStatus.FAIL,
                        TxStatus.NEED_CHECK,
                    ).joinToString(" "))
                }
            }
            request.keyword?.trim()?.split(" ")
                ?.let { it.toSet().map { "*$it*" }.joinToString(" ") }
                ?.let { value ->
                    must {
                        it.queryString {
                            it.fields(PurchaseHistory::prodNm,PurchaseHistory::description).query(value)
                                .analyzeWildcard(true)
                        }
                    }
                }
            request.fromDate?.let { it.toDate().atStartOfDay() }?.let { value ->
                must {
                    it.range { it.field(PurchaseHistory::createdAt).gte(value) }
                }
            }
            request.toDate?.let { it.toDate().atStartOfDay().plusDays(1) }?.let { value ->
                must {
                    it.range { it.field(PurchaseHistory::updatedAt).lt(JsonData.of(value.format())) }
                }
            }
            request.fromAmount?.let { value ->
                must {
                    it.range { it.field(PurchaseHistory::amount).gte(value) }
                }
            }
            request.toAmount?.let { value ->
                must {
                    it.range { it.field(PurchaseHistory::amount).lte(value) }
                }
            }
            request.userId?.let { value ->
                filter {
                    it.term { it.field(PurchaseHistory::userId).value(value) }
                }
            }
        }}}.withPageable(request.pageable).withSort {
            it.field {
                it.field(PurchaseHistory::createdAt).order(SortOrder.Desc)
            }
        }.withSearchAfter(request.nextKey)
            .build()

        val rs = esTemplate.searchForPage(nativeQuery, PurchaseHistory::class.java).awaitSingle()

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

fun QueryStringQuery.Builder.fields(vararg fields: KProperty<*>): QueryStringQuery.Builder {
    return this.fields(fields.map { it.name })
}

fun FieldSort.Builder.field(field: KProperty<*>): FieldSort.Builder {
    return this.field(field.name)
}

fun TermQuery.Builder.field(field: KProperty<*>): TermQuery.Builder {
    return this.field(field.name)
}

fun RangeQuery.Builder.field(field: KProperty<*>): RangeQuery.Builder {
    return this.field(field.name)
}

fun RangeQuery.Builder.gte(value: Long): RangeQuery.Builder {
    return this.gte(JsonData.of(value))
}

fun RangeQuery.Builder.gt(value: Long): RangeQuery.Builder {
    return this.gt(JsonData.of(value))
}

fun RangeQuery.Builder.lte(value: Long): RangeQuery.Builder {
    return this.lte(JsonData.of(value))
}

fun RangeQuery.Builder.lt(value: Long): RangeQuery.Builder {
    return this.lt(JsonData.of(value))
}

fun RangeQuery.Builder.gte(value: LocalDateTime): RangeQuery.Builder {
    return this.gte(JsonData.of(value.format()))
}

fun RangeQuery.Builder.gt(value: LocalDateTime): RangeQuery.Builder {
    return this.gt(JsonData.of(value.format()))
}

fun RangeQuery.Builder.lte(value: LocalDateTime): RangeQuery.Builder {
    return this.lte(JsonData.of(value.format()))
}

fun RangeQuery.Builder.lt(value: LocalDateTime): RangeQuery.Builder {
    return this.lt(JsonData.of(value.format()))
}

fun LocalDateTime.format(format: String = "yyyy-MM-dd'T'hh:mm:dd.SSS"): String {
    return this.format(DateTimeFormatter.ofPattern(format))
}

data class QrySearch(
    val userId: Long? = null,
    val keyword: String? = null,
    val fromDate: String? = null,
    val toDate: String? = null,
    val fromAmount: Long? = null,
    val toAmount: Long? = null,

    var size: Int = 20,
    var nextKey: List<Any>? = null,
) {
    val pageable: Pageable
        get() = PageRequest.of(0, size)
}

data class ResSearch(
    val histories: List<PurchaseHistory>,
    val total: Long,
    val nextKey: List<Any>?,
)
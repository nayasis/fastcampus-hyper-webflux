package dev.fastcampus.payment.service

import dev.fastcampus.payment.exception.NotFoundException
import dev.fastcampus.payment.model.Order
import dev.fastcampus.payment.model.code.TxStatus
import dev.fastcampus.payment.repository.OrderRepository
import dev.fastcampus.payment.repository.ProductRepository
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val logger = KotlinLogging.logger {}

@Service
class OrderService(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val dbclient: DatabaseClient,
) {

    @Transactional
    suspend fun create(userId: Long, prodId: Long): Order {
        val product = productRepository.findById(prodId) ?: throw NotFoundException("No product(id:$prodId) found")
        return orderRepository.save(Order(
            userId,
            prodId,
            product.price,
            product.localName,
        ))
    }

    suspend fun get(id: Long): Order {
        return orderRepository.findById(id) ?: throw NotFoundException("No order found (id: $id)")
    }

    suspend fun retrieve(request: QryOrder): List<ResPurchaseHistory> {

        val param = HashMap<String,Any>().apply {
            put("userId", request.userId)
            put("status", listOf(
                TxStatus.REQUEST_CONFIRM,
                TxStatus.SUCCESS,
                TxStatus.FAIL,
                TxStatus.NEED_CHECK,
            ).map { it.name })
            put("limit", request.limit)
            put("offset", (request.page - 1) * request.limit)
        }

        var sql = dbclient.sql("""
            SELECT  A.id,
                    A.prod_id,
                    B.name AS prod_nm,
                    A.description,
                    A.amount,
                    A.status,
                    A.created_at
            FROM    TB_ORDER A
            JOIN    TB_PROD  B
                    ON(A.prod_id = B.id)
            WHERE   1=1
            AND     A.user_id = :userId
            AND     A.status IN (:status)
            ${request.keyword.sql {
                val keywords = it.trim().split(" ")
                when(keywords.size) {
                    0 -> ""
                    else -> {
                        val phrase = mutableListOf<String>()
                        repeat(keywords.size) { i ->
                            val key = "keyword_$i"
                            param[key] = "%${keywords[i]}%"
                            phrase.add("( A.description LIKE :$key OR B.name LIKE :$key )")
                        }
                        "AND ${phrase.joinToString(" AND\n")}"                        
                    }
                }
            }}
            ${request.fromDate.sql {
                param["fromDate"] = it.toDate().atStartOfDay()
                "AND  A.created_at >= :fromDate"
            }}
            ${request.toDate.sql {
                param["toDate"] = it.toDate().plusDays(1).atStartOfDay()
                "AND  A.created_at < :toDate"
            }}
            ${request.fromAmount.sql {
                param["fromAmount"] = it
                "AND  A.amount >= :fromAmount"
            }}
            ${request.toAmount.sql {
                param["toAmount"] = it
                "AND  A.amount <= :toAmount"
            }}
            ORDER BY A.created_at DESC
            LIMIT :limit OFFSET :offset
        """.trimIndent())

        logger.debug { ">> parameter\n$param" }

        param.forEach { key, value -> sql = sql.bind(key,value)  }



        return sql.map { row, _ ->
            ResPurchaseHistory(
                orderId     = row.get("id") as Long,
                prodId      = row.get("prod_id") as Long,
                prodNm      = row.get("prod_nm") as String,
                description = (row.get("description") as? String) ?: "",
                amount      = row.get("amount") as Long,
                status      = (row.get("status") as String).let { TxStatus.valueOf(it) },
                createdAt   = row.get("created_at") as LocalDateTime,
            )
        }.flow().toList()

    }

}

data class QryOrder(
    val userId: Long,
    val keyword: String?,
    val fromDate: String?,
    val toDate: String?,
    val fromAmount: Long?,
    val toAmount: Long?,
    val limit: Long = 10,
    val page: Long = 1,
)

private fun <T> T?.sql(function: (value: T) -> String): String {
    return when {
        this == null -> ""
        this is String && this.isEmpty() -> ""
        else -> function.invoke(this)
    }
}

fun String.toDate(): LocalDate {
    return LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}

data class ResPurchaseHistory(
    val orderId: Long,
    val prodId: Long,
    val prodNm: String,
    val description: String,
    val amount: Long,
    val status: TxStatus,
    val createdAt: LocalDateTime,
)
package dev.fastcampus.elasticsearch.common

import co.elastic.clients.elasticsearch._types.FieldSort
import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery
import co.elastic.clients.json.JsonData
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.core.query.Criteria
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KProperty

val KProperty<*>.criteria: Criteria
    get() = Criteria(this.name)

fun KProperty<*>.sort(direction: Sort.Direction = Sort.Direction.ASC): Sort = Sort.by(direction, this.name)

fun LocalDateTime.asString(format: String = "yyyy-MM-dd'T'hh:mm:dd.SSS"): String {
    return this.format(DateTimeFormatter.ofPattern(format))
}

fun String.toLocalDate(format: String = "yyyy-MM-dd"): LocalDate {
    return LocalDate.parse(this, DateTimeFormatter.ofPattern(format))
}

fun String.toLocalDateTime(format: String = "yyyy-MM-dd"): LocalDateTime {
    return this.toLocalDate(format).atStartOfDay()
}

fun QueryStringQuery.Builder.fields(vararg fields: KProperty<*>): QueryStringQuery.Builder {
    return this.fields(fields.map { it.name })
}

fun FieldSort.Builder.field(field: KProperty<*>): FieldSort.Builder {
    return this.field(field.name)
}

fun MatchQuery.Builder.field(field: KProperty<*>): MatchQuery.Builder {
    return this.field(field.name)
}

fun TermQuery.Builder.field(field: KProperty<*>): TermQuery.Builder {
    return this.field(field.name)
}

fun TermsQuery.Builder.field(field: KProperty<*>): TermsQuery.Builder {
    return this.field(field.name)
}

fun TermsQuery.Builder.values(collection: Collection<*>): TermsQuery.Builder {
    return this.terms { it.value(collection.map { FieldValue.of("$it") }) }
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
    return this.gte(JsonData.of(value.asString()))
}

fun RangeQuery.Builder.gt(value: LocalDateTime): RangeQuery.Builder {
    return this.gt(JsonData.of(value.asString()))
}

fun RangeQuery.Builder.lte(value: LocalDateTime): RangeQuery.Builder {
    return this.lte(JsonData.of(value.asString()))
}

fun RangeQuery.Builder.lt(value: LocalDateTime): RangeQuery.Builder {
    return this.lt(JsonData.of(value.asString()))
}
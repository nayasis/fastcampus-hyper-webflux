package dev.fastcampus.payment.model.converter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.DialectResolver

@Configuration
class ConverterConfig(
    private val objectMapper: ObjectMapper
) {

    fun fromSetToString(): Converter<Set<String>?, String?> {
        return object: Converter<Set<String>?, String?> {
            override fun convert(source: Set<String>): String {
                return objectMapper.writeValueAsString(source)
            }

        }
    }

    fun fromStringToSet(): Converter<String?, HashSet<String>?> {
        return object: Converter<String?,HashSet<String>?> {
            override fun convert(source: String): HashSet<String>? {
                return objectMapper.readValue(source, object: TypeReference<HashSet<String>>(){})
            }

        }
    }

    @Bean
    fun converters(connectionFactory: ConnectionFactory): R2dbcCustomConversions {
        val dialect = DialectResolver.getDialect(connectionFactory);
        return R2dbcCustomConversions.of( dialect, listOf(
            fromSetToString(),
            fromStringToSet(),
        ))
    }

}
package dev.fastcampus.payment.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import javax.annotation.processing.Generated

@Table("TB_PROD")
class Product {

    @Id
    @Generated
    var id: Long = 0

    var name: String = ""

    var price: Long = 0

    var hashtag: Set<String>? = null

    @CreatedDate
    var createdAt: LocalDateTime? = null

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null

    constructor()
    constructor(name: String, price: Long, hashtag: Set<String>? = null) {
        this.name    = name
        this.price   = price
        this.hashtag = hashtag
    }

    override fun equals( other:Any? ): Boolean = kotlinEquals(other, arrayOf(Product::id))
    override fun hashCode(): Int = kotlinHashCode(arrayOf(Product::id))
    override fun toString(): String {
        return "Product(id=$id, name='$name', price=$price, hashtag=$hashtag, createdAt=$createdAt, updatedAt=$updatedAt)"
    }

}
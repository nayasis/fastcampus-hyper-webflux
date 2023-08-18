package dev.fastcampus.payment.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import com.fasterxml.jackson.annotation.JsonIgnore
import dev.fastcampus.payment.model.parent.BaseEntity
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table

@Table("TB_PROD")
class Product(
    @Id
    var id: Long = 0,
    var name: String = "",
    var localName: String = "",
    var price: Long = 0,
): BaseEntity(), Persistable<Long> {

    constructor(name: String, price: Long): this() {
        this.name  = name
        this.price = price
    }

    override fun equals( other:Any? ): Boolean = kotlinEquals(other, arrayOf(Product::id))
    override fun hashCode(): Int = kotlinHashCode(arrayOf(Product::id))
    override fun toString(): String = kotlinToString(arrayOf(
        Product::id,
        Product::name,
        Product::price,
        Product::localName,
    ), superToString = {super.toString()})


    override fun getId(): Long = id

//    // transient not works in R2DBC (https://github.com/spring-projects/spring-data-r2dbc/issues/449)
//    @Transient
    @Value("null")
    @JsonIgnore
    var new: Boolean = false

    override fun isNew(): Boolean {
        return new
    }

}
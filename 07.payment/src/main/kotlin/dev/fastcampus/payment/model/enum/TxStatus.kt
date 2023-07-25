package dev.fastcampus.payment.model.enum

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class TxStatus(val code: Int, val desc: String) {

    NONE(0, "none"),
    CREATE(1, "create"),
    REQUEST_CONFIRM(2, "request confirm"),
    SUCCESS(3, "success"),
    FAIL(4, "fail"),
    ;

    companion object {
        fun of(code: Int): TxStatus {
            return values().firstOrNull { it.code == code } ?: NONE
        }
    }

}
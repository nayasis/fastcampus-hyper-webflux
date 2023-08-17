package dev.fastcampus.payment.model.enum

enum class TxStatus {
    NONE,
    CREATE,
    REQUEST_CONFIRM,
    SUCCESS,
    FAIL,
    NEED_CHECK
    ;
}
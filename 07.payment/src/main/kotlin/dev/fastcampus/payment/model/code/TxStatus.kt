package dev.fastcampus.payment.model.code

enum class TxStatus {
    NONE,
    CREATE,
    INVALID_REQUEST,
    REQUEST_CONFIRM,
    SUCCESS,
    FAIL,
    NEED_CHECK
    ;
}
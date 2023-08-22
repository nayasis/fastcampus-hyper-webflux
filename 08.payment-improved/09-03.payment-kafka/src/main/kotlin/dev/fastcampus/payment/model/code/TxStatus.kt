package dev.fastcampus.payment.model.code

enum class TxStatus {
    NONE,
    CREATE,
    REQUEST_CONFIRM,
    SUCCESS,
    FAIL,
    NEED_CHECK
    ;
}
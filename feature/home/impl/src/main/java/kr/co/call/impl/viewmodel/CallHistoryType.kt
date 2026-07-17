package kr.co.call.impl.viewmodel

enum class CallHistoryType {
    SEND,
    RECEIVED,
    SEND_MISSED,
    RECEIVE_MISSED,
}

internal val CallHistoryType.isMissed: Boolean
    get() = this == CallHistoryType.SEND_MISSED ||
        this == CallHistoryType.RECEIVE_MISSED

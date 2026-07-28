package kr.co.call.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class CallIncomingNavKey(
    val callId: Long,
    val characterId: Long,
) : NavKey

@Serializable
data class CallSendingNavKey(
    val characterId: Long,
) : NavKey

data class IncomingCallInfo(
    val callId: Long,
    val characterId: Long,
)

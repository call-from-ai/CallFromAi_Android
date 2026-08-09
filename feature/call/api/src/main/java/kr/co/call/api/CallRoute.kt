package kr.co.call.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class CallIncomingNavKey(
    val callId: Long,
    val characterId: Long,
    val characterName: String = "",
    val characterImageUrl: String? = null,
) : NavKey

/**
 * 수신 수락이 완료된 뒤 실제 통화 화면으로 이동할 때 사용합니다.
 */
@Serializable
data class CallActiveNavKey(
    val callId: Long,
    val characterId: Long,
    val isIncoming: Boolean,
    val characterName: String = "",
    val characterImageUrl: String? = null,
) : NavKey

@Serializable
data class CallSendingNavKey(
    val characterId: Long,
    val characterName: String = "",
    val characterImageUrl: String? = null,
) : NavKey

package kr.co.call.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class CallRecordNavKey(
    val callId: Long,
) : NavKey
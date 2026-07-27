package kr.co.call.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class CallNavKey(
    val characterId: Long,
    val characterName: String,
) : NavKey

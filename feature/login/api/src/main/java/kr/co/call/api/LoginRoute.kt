package kr.co.call.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * 랜딩 화면 NavKey
 */
@Serializable
data object LandingNavKey: NavKey
/**
 * 로그인 화면 NavKey
 */
@Serializable
data object LoginNavKey : NavKey
/**
 * 약관 동의 화면 NavKey
 */
@Serializable
data object AgreementNavKey: NavKey
/**
 * 약관 상세 화면 NavKey
 */
@Serializable
data class AgreementDetailNavKey(
    val termId: Long,
    val title: String,
    val content: String,
):NavKey
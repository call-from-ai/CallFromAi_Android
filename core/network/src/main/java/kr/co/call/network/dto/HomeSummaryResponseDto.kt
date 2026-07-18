package kr.co.call.network.dto

data class HomeSummaryResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: HomeSummaryDto,
)

data class HomeSummaryDto(
    val firstName: String,
    val relationshipDays: Int,
    val totalCallCount: Int,
    val callStreakDays: Int,
)

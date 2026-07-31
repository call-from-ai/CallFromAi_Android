package kr.co.call.network.dto.home

data class HomeSummaryDto(
    val firstName: String,
    val relationshipDays: Int,
    val totalCallCount: Int,
    val callStreakDays: Int,
    val characterId: Long,
)

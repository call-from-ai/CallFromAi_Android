package kr.co.call.domain.model.home

data class HomeSummary(
    val firstName: String,
    val relationshipDays: Int,
    val totalCallCount: Int,
    val callStreakDays: Int,
)

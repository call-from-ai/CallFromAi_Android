package kr.co.call.impl.viewmodel.state

data class HomeSummaryState(
    val characterId: Long = 0L,
    val firstName: String = "수현",
    val relationshipDays: Int = 30,
    val totalCallCount: Int = 24,
    val callStreakDays: Int = 12,
)
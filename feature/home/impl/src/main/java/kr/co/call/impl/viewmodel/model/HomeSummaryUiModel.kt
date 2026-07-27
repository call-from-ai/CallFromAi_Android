package kr.co.call.impl.viewmodel.model

data class HomeSummaryUiModel(
    val firstName: String = "",
    val relationshipDaysText: String = "-",
    val totalCallCountText: String = "-",
    val callStreakDaysText: String = "-",
)

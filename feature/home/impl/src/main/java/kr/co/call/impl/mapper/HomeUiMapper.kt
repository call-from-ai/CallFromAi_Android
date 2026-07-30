package kr.co.call.impl.mapper

import kr.co.call.domain.model.home.HomeCharacter
import kr.co.call.domain.model.home.HomeSummary
import kr.co.call.impl.viewmodel.model.CharacterOptionUiModel
import kr.co.call.impl.viewmodel.model.HomeSummaryUiModel

fun HomeSummary.toUiModel(): HomeSummaryUiModel =
    HomeSummaryUiModel(
        firstName = firstName,
        relationshipDaysText = relationshipDays.withSuffixOrDash("일째"),
        totalCallCountText = totalCallCount.withSuffixOrDash("회"),
        callStreakDaysText = callStreakDays.withSuffixOrDash("일"),
    )

fun HomeCharacter.toUiModel(): CharacterOptionUiModel =
    CharacterOptionUiModel(
        characterId = id,
        name = name,
        day = relationshipDays,
        imageUrl = imageUrl,
        isSelected = isMain,
    )

private fun Int.withSuffixOrDash(suffix: String): String =
    if (this == 0) "-" else "$this$suffix"

package kr.co.call.data.mapper

import kr.co.call.domain.model.home.HomeSummary
import kr.co.call.network.dto.HomeSummaryDto

internal fun HomeSummaryDto.toDomain(): HomeSummary =
    HomeSummary(
        firstName = firstName,
        relationshipDays = relationshipDays,
        totalCallCount = totalCallCount,
        callStreakDays = callStreakDays,
    )

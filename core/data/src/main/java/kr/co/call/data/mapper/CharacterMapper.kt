package kr.co.call.data.mapper

import java.time.format.DateTimeFormatter
import kr.co.call.core.common.util.TimeUtil
import kr.co.call.domain.model.home.HomeCharacter
import kr.co.call.domain.model.mypage.AiCharacter
import kr.co.call.network.dto.character.MyCharacterDto

private val characterCreatedAtFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy.MM.dd")

internal fun MyCharacterDto.toAiCharacter(): AiCharacter {
    val createdAtDate = TimeUtil.parseLocalDateTime(createdAt).toLocalDate()
    val lastConversationLabel = lastMessageAt
        ?.takeIf { it.isNotBlank() }
        ?.let { TimeUtil.toTimeAgoText(TimeUtil.parseLocalDateTime(it)) }
        ?: "대화 없음"

    return AiCharacter(
        id = characterId.toString(),
        name = name,
        profileImageUrl = imageUrl.orEmpty(),
        isMain = main,
        createdAtLabel = createdAtDate.format(characterCreatedAtFormatter),
        daysTogetherLabel = "${daysTogether}일 째",
        lastConversationLabel = lastConversationLabel,
        summary = "",
    )
}

internal fun MyCharacterDto.toHomeCharacter(): HomeCharacter =
    HomeCharacter(
        id = characterId,
        name = name,
        relationshipDays = daysTogether,
        imageUrl = imageUrl.orEmpty(),
        isMain = main,
    )

package kr.co.call.impl.mapper

import kr.co.call.core.common.util.TimeUtil
import kr.co.call.domain.model.home.HomeNotification
import kr.co.call.impl.viewmodel.model.HomeNotificationUiModel

fun HomeNotification.toUiModel(): HomeNotificationUiModel =
    HomeNotificationUiModel(
        notificationId = notificationId,
        type = type,
        title = title,
        content = content,
        isRead = isRead,
        createdAtText = TimeUtil.toTimeAgoText(createdAt),
        characterName = characterName,
        profileImageUrl = profileImageUrl,
    )

package kr.co.call.impl.mapper

import kr.co.call.core.common.util.TimeUtil
import kr.co.call.domain.model.home.CallInfo
import kr.co.call.impl.viewmodel.model.CallRecordUiModel

fun CallInfo.toUiModel(): CallRecordUiModel =
    CallRecordUiModel(
        title = title,
        calledAtText = TimeUtil.toCallHistoryDateText(calledAt),
        characterName = characterName,
        recordingUrl = recordingUrl,
    )

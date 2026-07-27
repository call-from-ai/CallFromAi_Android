package kr.co.call.impl.viewmodel.state

import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.viewmodel.model.CallCharacterUiModel

data class CallIncomingState(
    val callId: String = "",
    val character: CallCharacterUiModel = CallCharacterUiModel(),
    val loadStatus: LoadStatus = LoadStatus.Idle,
)

package kr.co.call.impl.viewmodel.state

import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.viewmodel.model.CallCharacterUiModel

data class CallIncomingState(
    val callId: Long = 0L,
    val characterId: Long = 0L,
    val character: CallCharacterUiModel = CallCharacterUiModel(),
    val loadStatus: LoadStatus = LoadStatus.Idle,
)

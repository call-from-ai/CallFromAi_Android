package kr.co.call.impl.viewmodel.state

import kr.co.call.domain.model.home.CallTranscript
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.viewmodel.model.CallRecordUiModel

data class CallRecordState(
    val callId: Long? = null,
    val record: CallRecordUiModel = CallRecordUiModel(),
    val transcripts: List<CallTranscript> = emptyList(),
    val loadStatus: LoadStatus = LoadStatus.Loading,
)

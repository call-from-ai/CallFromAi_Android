package kr.co.call.impl.viewmodel

import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.component.PreferTime

data class CallTimeManagementState(
    val loadStatus: LoadStatus = LoadStatus.Idle,
    val selected: PreferTime? = null,
    val isSaving: Boolean = false,
) {
    val canComplete: Boolean
        get() = selected != null &&
            !isSaving &&
            loadStatus !is LoadStatus.Loading
}

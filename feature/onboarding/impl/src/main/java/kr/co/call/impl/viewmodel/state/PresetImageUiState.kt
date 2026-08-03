package kr.co.call.impl.viewmodel.state

import kr.co.call.domain.model.onboarding.PresetImage
import kr.co.call.domain.util.LoadStatus

data class PresetImageUiState(
    val maleImages: List<PresetImage> = emptyList(),
    val femaleImages: List<PresetImage> = emptyList(),
    val isMaleImagesLoaded: Boolean = false,
    val isFemaleImagesLoaded: Boolean = false,
    val loadStatus: LoadStatus = LoadStatus.Idle,
)

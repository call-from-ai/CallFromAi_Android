package kr.co.call.impl.viewmodel

import java.time.LocalDate
import kr.co.call.api.OnboardingFlowMode
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.component.PreferTime
import kr.co.call.impl.viewmodel.model.Relationship
import kr.co.call.impl.viewmodel.model.SpeechStyle
import kr.co.call.impl.viewmodel.model.Trait
import kr.co.call.impl.viewmodel.state.PresetImageUiState

data class OnboardingUiState(
    val flowMode: OnboardingFlowMode = OnboardingFlowMode.FIRST_ONBOARDING,

    // 사용자 정보
    val userLastName: String = "",
    val userFirstName: String = "",
    val userBirthday: LocalDate =LocalDate.now(),
    val userJob: String = "",
    val userMbti: String = "",
    val userGender: String = "",
    val userImageUrl: String = "",

    // 이상형 정보
    val aiFirstName: String = "",
    val aiLastName: String = "",
    val aiAge: String = "",
    val aiJob: String = "",
    val aiMbti: String = "",
    val aiGender: String = "",
    val aiImageUrl: String = "",

    // 3단계
    val speechStyle: SpeechStyle? = null,
    val relationship: Relationship? = null,
    val temperature: Int = 50,

    // 4단계
    val traits: List<Trait> = emptyList(),

    // 5단계
    val preferTime: PreferTime? = null,

    val isCreatingAi: Boolean = false,
    val createAiError: String? = null,
    val submitStatus: LoadStatus = LoadStatus.Idle,
    val isMemberSubmitted: Boolean = false,

    val presetImageState: PresetImageUiState = PresetImageUiState(),

    // 온보딩6 / 생성 결과
    val createdAiId: Long? = null,
    val createdAiName: String = "",
)

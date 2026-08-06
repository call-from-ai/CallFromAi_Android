package kr.co.call.impl.viewmodel

import kr.co.call.domain.model.onboarding.PresetImage
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.component.PreferTime
import kr.co.call.impl.viewmodel.model.Trait
import kr.co.call.impl.viewmodel.model.Relationship
import kr.co.call.impl.viewmodel.model.SpeechStyle
import kr.co.call.impl.viewmodel.state.PresetImageUiState
import java.time.LocalDate

data class OnboardingUiState(
    //사용자 정보
    val userLastName: String = "",
    val userFirstName: String = "",
    val userBirthday: LocalDate? =null,
    val userJob: String = "",
    val userMbti: String = "",
    val userGender: String="",
    val userImageUrl: String="",

    //이상형 정보
    val aiFirstName: String = "",
    val aiLastName: String = "",
    val aiAge: String = "",
    val aiJob: String = "",
    val aiMbti: String = "",
    val aiGender: String="",
    val aiImageUrl: String="",

    //3단계
    val speechStyle: SpeechStyle? = null,
    val relationship: Relationship? = null,
    val temperature: Int = 50,

    //4단계
    val traits: List<Trait> = emptyList(),

    //5단계
    val preferTime: PreferTime? = null,

    val isCreatingAi: Boolean=false,
    val createAiError: String?=null,
    val submitStatus: LoadStatus= LoadStatus.Idle,
    val isMemberSubmitted: Boolean = false,

    val presetImageState: PresetImageUiState= PresetImageUiState(),

    //온보딩6스크린 ai캐릭터이름 받아오기
    val createdAiId: Long? = null,
    val createdAiName: String = "",
)

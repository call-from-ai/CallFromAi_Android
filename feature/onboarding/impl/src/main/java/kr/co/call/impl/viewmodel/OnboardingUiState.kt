package kr.co.call.impl.viewmodel

import androidx.compose.runtime.Composable
import kr.co.call.impl.component.PreferTime
import kr.co.call.impl.component.Trait
import kr.co.call.impl.screen.Relationship
import kr.co.call.impl.screen.SpeechStyle
import java.time.LocalDate

data class OnboardingUiState(
    //사용자 정보
    val userLastName: String = "",
    val userFirstName: String = "",
    val userBirthday: LocalDate = LocalDate.of(2000, 1, 1),
    val userJob: String = "",
    val userMbti: String = "",

    //이상형 정보
    val aiFirstName: String = "",
    val aiLastName: String = "",
    val aiAge: String = "",
    val aiJob: String = "",
    val aiMbti: String = "",

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
)
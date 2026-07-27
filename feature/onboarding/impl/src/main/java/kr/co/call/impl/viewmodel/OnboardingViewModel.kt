package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kr.co.call.impl.component.PreferTime
import kr.co.call.impl.component.Trait
import kr.co.call.impl.screen.Relationship
import kr.co.call.impl.screen.SpeechStyle
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()

    fun updateUserProfile(
        lastName: String,
        firstName: String,
        birthday: LocalDate,
        job: String,
        mbti: String,
    ) {
        _uiState.update {
            it.copy(
                userLastName = lastName,
                userFirstName = firstName,
                userBirthday = birthday,
                userJob = job,
                userMbti = mbti,
            )
        }
    }

    fun updateAiProfile(
        age: String,
        lastName: String,
        firstName: String,
        job: String,
        mbti: String,
    ) {
        _uiState.update {
            it.copy(
                aiFirstName = firstName,
                aiLastName = lastName,
                aiAge = age,
                aiJob = job,
                aiMbti = mbti,
            )
        }
    }


    fun updateConversationStyle(
        speechStyle: SpeechStyle,
        relationship: Relationship,
        temperature: Int,
    ) {
        _uiState.update {
            it.copy(
                speechStyle = speechStyle,
                relationship = relationship,
                temperature = temperature,
            )
        }
    }


    fun updateTraits(traits: List<Trait>) {
        _uiState.update {
            it.copy(traits = traits)
        }
    }

    fun updatePreferTime(preferTime: PreferTime) {
        _uiState.update {
            it.copy(preferTime = preferTime)
        }
    }
}
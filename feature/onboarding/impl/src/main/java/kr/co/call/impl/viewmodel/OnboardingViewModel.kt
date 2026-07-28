package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.call.impl.component.PreferTime
import kr.co.call.impl.component.Trait
import kr.co.call.impl.screen.Relationship
import kr.co.call.impl.screen.SpeechStyle
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel(),
    ContainerHost<OnboardingUiState, Nothing> {
        override val container=container<OnboardingUiState, Nothing>(
            initialState= OnboardingUiState(),
        )

    fun updateUserProfile(
        lastName: String,
        firstName: String,
        birthday: LocalDate,
        job: String,
        mbti: String,
    ) =intent{
        reduce{
                state.copy(
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
    )=intent {
        reduce {
            state.copy(
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
    ) =intent {
        reduce {
            state.copy(
                speechStyle = speechStyle,
                relationship = relationship,
                temperature = temperature,
            )
        }
    }


    fun updateTraits(traits: List<Trait>
    )=intent{
        reduce {
            state.copy(traits = traits)
        }
    }

    fun updatePreferTime(preferTime: PreferTime
    )=intent {
        reduce {
            state.copy(preferTime = preferTime)
        }
    }
}
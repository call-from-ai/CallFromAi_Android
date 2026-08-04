package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.call.designsystem.component.profileimage.ProfileImageGender
import kr.co.call.domain.exception.toUserMessage
import kr.co.call.domain.model.onboarding.CharacterOnboardingInput
import kr.co.call.domain.model.onboarding.CharacterTraitInput
import kr.co.call.domain.model.onboarding.CreatedCharacter
import kr.co.call.domain.model.onboarding.MemberOnboardingInput
import kr.co.call.domain.repository.OnboardingRepository
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.component.PreferTime
import kr.co.call.impl.viewmodel.model.Trait
import kr.co.call.impl.viewmodel.model.Relationship
import kr.co.call.impl.viewmodel.model.SpeechStyle
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) : ViewModel(),
    ContainerHost<OnboardingUiState, OnboardingSideEffect> {
    override val container =
        container<OnboardingUiState, OnboardingSideEffect>(
            initialState = OnboardingUiState(),
        )

    fun updateUserProfile(
        lastName: String,
        firstName: String,
        birthday: LocalDate,
        job: String,
        mbti: String,
        gender: String,
        imageUrl: String,
    ) = intent {
        reduce {
            state.copy(
                userLastName = lastName,
                userFirstName = firstName,
                userBirthday = birthday,
                userJob = job,
                userMbti = mbti,
                userGender = gender,
                userImageUrl = imageUrl,
            )
        }
    }

    fun updateAiProfile(
        age: String,
        lastName: String,
        firstName: String,
        job: String,
        mbti: String,
        gender: String,
        imageUrl: String,
    ) = intent {
        reduce {
            state.copy(
                aiFirstName = firstName,
                aiLastName = lastName,
                aiAge = age,
                aiJob = job,
                aiMbti = mbti,
                aiGender = gender,
                aiImageUrl = imageUrl,
            )
        }
    }


    fun updateConversationStyle(
        speechStyle: SpeechStyle,
        relationship: Relationship,
        temperature: Int,
    ) = intent {
        reduce {
            state.copy(
                speechStyle = speechStyle,
                relationship = relationship,
                temperature = temperature,
            )
        }
    }


    fun updateTraits(
        traits: List<Trait>
    ) = intent {
        reduce {
            state.copy(traits = traits)
        }
    }

    //
    fun updatePreferTime(
        preferTime: PreferTime
    ) = intent {
        reduce {
            state.copy(preferTime = preferTime)
        }
    }

    fun submitOnboarding(
        preferTime: PreferTime,
    ) = intent {
        if (state.submitStatus == LoadStatus.Loading) return@intent

        val age = state.aiAge.toIntOrNull()
        val speechStyle = state.speechStyle
        val relationship = state.relationship
        if (
            age == null ||
            speechStyle == null ||
            relationship == null ||
            state.traits.isEmpty()
        ) {
            postSideEffect(
                OnboardingSideEffect.ShowMessage(
                    message = "온보딩 정보를 확인해주세요.",
                ),
            )
            return@intent
        }

        reduce {
            state.copy(
                preferTime = preferTime,
                submitStatus = LoadStatus.Loading,
            )
        }

        val memberSubmission = MemberOnboardingInput(
            lastName = state.userLastName,
            firstName = state.userFirstName,
            imageUrl = state.userImageUrl,
            gender = state.userGender,
            birth = state.userBirthday.toString(),
            mbti = state.userMbti,
            job = state.userJob,
        )

        val characterSubmission = CharacterOnboardingInput(
            lastName = state.aiLastName,
            firstName = state.aiFirstName,
            gender = state.aiGender,
            age = age,
            job = state.aiJob,
            imageUrl = state.aiImageUrl,
            spiceLevel = state.temperature,
            preferTime = preferTime.name,
            mbti = state.aiMbti,
            speechStyle = speechStyle.name,
            relationshipStage = relationship.name,
            traits = state.traits.mapIndexed { index, trait ->
                CharacterTraitInput(
                    trait = trait.keyword,
                    priority = index + 1,
                )
            },
        )

        val submitResult: Result<CreatedCharacter> =
            onboardingRepository.submitMemberOnboarding(memberSubmission)
                .fold(
                    onSuccess = {
                        onboardingRepository.submitCharacterOnboarding(characterSubmission)
                    },
                    onFailure = { error ->
                        Result.failure(error)
                    },
                )

        submitResult
            .onSuccess { character ->
                reduce {
                    state.copy(
                        createdAiId = character.id,
                        createdAiName = character.name,
                        submitStatus = LoadStatus.Idle
                    )
                }

                postSideEffect(
                    OnboardingSideEffect.OnboardingSubmitted,
                )
            }
            .onFailure { error ->
                if (error is CancellationException) throw error

                reduce {
                    state.copy(submitStatus = LoadStatus.Idle)
                }

                postSideEffect(
                    OnboardingSideEffect.ShowMessage(
                        message = error.toUserMessage(
                            default = "온보딩 저장에 실패했습니다.",
                        ),
                    ),
                )
            }
    }

    fun loadPresetImages(
        gender: ProfileImageGender,
    ) = intent {
        val isAlreadyLoaded = when (gender) {
            ProfileImageGender.MALE -> state.presetImageState.isMaleImagesLoaded
            ProfileImageGender.FEMALE -> state.presetImageState.isFemaleImagesLoaded
        }

        if (isAlreadyLoaded) return@intent
        if (state.presetImageState.loadStatus == LoadStatus.Loading) return@intent
        reduce {
            state.copy(
                presetImageState = state.presetImageState.copy(
                    loadStatus = LoadStatus.Loading,
                    ),
            )
        }
        onboardingRepository
            .getPresetImages(gender.name)
            .onSuccess { images ->
                reduce {
                    when (gender) {
                        ProfileImageGender.MALE -> state.copy(
                            presetImageState =
                                state.presetImageState.copy(
                                    maleImages = images,
                                    isMaleImagesLoaded = true,
                                    loadStatus = LoadStatus.Idle,
                                ),
                        )

                        ProfileImageGender.FEMALE -> state.copy(
                            presetImageState =
                                state.presetImageState.copy(
                                    femaleImages = images,
                                    isFemaleImagesLoaded = true,
                                    loadStatus = LoadStatus.Idle,
                                ),
                        )
                    }
                }
            }
            .onFailure { error ->
                reduce {
                    state.copy(
                        presetImageState = state.presetImageState.copy(
                            loadStatus=LoadStatus.Idle,
                            ),
                    )
                }
            }
    }
}


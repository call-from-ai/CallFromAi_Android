package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kr.co.call.api.OnboardingFlowMode
import kr.co.call.designsystem.component.profileimage.ProfileImageGender
import kr.co.call.domain.exception.AppException
import kr.co.call.domain.exception.toUserMessage
import kr.co.call.domain.model.onboarding.CharacterOnboardingInput
import kr.co.call.domain.model.onboarding.CharacterTraitInput
import kr.co.call.domain.model.onboarding.CreatedCharacter
import kr.co.call.domain.model.onboarding.MemberOnboardingInput
import kr.co.call.domain.repository.OnboardingRepository
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.component.PreferTime
import kr.co.call.impl.viewmodel.model.Relationship
import kr.co.call.impl.viewmodel.model.SpeechStyle
import kr.co.call.impl.viewmodel.model.Trait
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
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

    /**
     * 플로우 진입 시 모드 설정 + 이상형(캐릭터) draft 초기화.
     * 프리셋 이미지는 재사용한다.
     */
    fun prepareFlow(mode: OnboardingFlowMode) = intent {
        reduce {
            state.copy(
                flowMode = mode,
                aiFirstName = "",
                aiLastName = "",
                aiAge = "",
                aiJob = "",
                aiMbti = "",
                aiGender = "",
                aiImageUrl = "",
                speechStyle = null,
                relationship = null,
                temperature = 50,
                traits = emptyList(),
                preferTime = null,
                isCreatingAi = false,
                createAiError = null,
                submitStatus = LoadStatus.Idle,
                // 이전 온보딩의 회원 제출 플래그가 남지 않도록 초기화
                isMemberSubmitted = false,
                createdAiId = null,
                createdAiName = null,
                createdAiImageUrl = null,
                isCallDialogVisible = false,
            )
        }
    }

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
        traits: List<Trait>,
    ) = intent {
        reduce {
            state.copy(traits = traits)
        }
    }

    fun updatePreferTime(
        preferTime: PreferTime,
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

        val isAddCharacter = state.flowMode == OnboardingFlowMode.ADD_CHARACTER

        val submitResult: Result<CreatedCharacter> =
            if (isAddCharacter) {
                // 캐릭터 추가: 회원 프로필 재제출 없이 캐릭터만 생성
                onboardingRepository.submitCharacterOnboarding(characterSubmission)
            } else {
                val memberSubmission = MemberOnboardingInput(
                    lastName = state.userLastName,
                    firstName = state.userFirstName,
                    imageUrl = state.userImageUrl,
                    gender = state.userGender,
                    birth = state.userBirthday.toString(),
                    mbti = state.userMbti,
                    job = state.userJob,
                )

                val memberSubmitResult =
                    if (state.isMemberSubmitted) {
                        Result.success(Unit)
                    } else {
                        onboardingRepository.submitMemberOnboarding(memberSubmission)
                            .recoverCatching { error ->
                                if (
                                    error is AppException.Conflict &&
                                    error.code == "MEMBER409_1"
                                ) {
                                    Unit
                                } else {
                                    throw error
                                }
                            }
                            .onSuccess {
                                reduce {
                                    state.copy(isMemberSubmitted = true)
                                }
                            }
                    }

                memberSubmitResult
                    .onFailure { error ->
                        Timber.e(error, "회원정보 생성 실패")
                    }
                    .fold(
                        onSuccess = {
                            onboardingRepository.submitCharacterOnboarding(characterSubmission)
                                .onFailure { error ->
                                    Timber.e(error, "캐릭터 생성 실패")
                                }
                        },
                        onFailure = { error -> Result.failure(error) },
                    )
            }

        submitResult
            .onSuccess { character ->
                reduce {
                    state.copy(
                        createdAiId = character.id,
                        createdAiName = character.name,
                        createdAiImageUrl = character.imageUrl,
                        submitStatus = LoadStatus.Idle,
                    )
                }
                postSideEffect(
                    if (isAddCharacter) {
                        OnboardingSideEffect.AdditionalCharacterCreated
                    } else {
                        OnboardingSideEffect.OnboardingSubmitted
                    },
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
                            default = if (isAddCharacter) {
                                "캐릭터 생성에 실패했습니다."
                            } else {
                                "온보딩 저장에 실패했습니다."
                            },
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
                    val updatedPresetState = when (gender) {
                        ProfileImageGender.MALE -> {
                            state.presetImageState.copy(
                                maleImages = images,
                                isMaleImagesLoaded = true,
                                loadStatus = LoadStatus.Idle,
                            )
                        }

                        ProfileImageGender.FEMALE -> {
                            state.presetImageState.copy(
                                femaleImages = images,
                                isFemaleImagesLoaded = true,
                                loadStatus = LoadStatus.Idle,
                            )
                        }
                    }

                    state.copy(
                        presetImageState = updatedPresetState,
                    )
                }
            }
            .onFailure {
                reduce {
                    state.copy(
                        presetImageState = state.presetImageState.copy(
                            loadStatus = LoadStatus.Idle,
                        ),
                    )
                }
            }
    }

    // 캐릭터 생성 완료 화면에서 바로 전화 연결 시 다이얼로그 표시
    fun showCallDialog() = intent {
        reduce {
            state.copy(isCallDialogVisible = true)
        }
    }

    // 다이얼로그 취소 시 다이얼로그 닫기
    fun hideCallDialog() = intent {
        reduce {
            state.copy(isCallDialogVisible = false)
        }
    }
}

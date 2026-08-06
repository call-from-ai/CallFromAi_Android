package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kr.co.call.designsystem.component.profileimage.ProfileImageGender
import kr.co.call.designsystem.component.profileimage.ProfileImageOption
import kr.co.call.domain.exception.AppException
import kr.co.call.domain.exception.toUserMessage
import kr.co.call.domain.model.mypage.CharacterTraitDetail
import kr.co.call.domain.model.mypage.CharacterUpdateInput
import kr.co.call.domain.repository.AICharacterRepository
import kr.co.call.domain.repository.OnboardingRepository
import kr.co.call.domain.util.LoadStatus
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class EditCharacterViewModel @Inject constructor(
    private val aiCharacterRepository: AICharacterRepository,
    private val onboardingRepository: OnboardingRepository,
) : ViewModel(), ContainerHost<EditCharacterState, EditCharacterSideEffect> {

    override val container: Container<EditCharacterState, EditCharacterSideEffect> = container(
        initialState = EditCharacterState(),
    )

    private var initialized = false

    fun initialize(characterId: Long) {
        if (initialized) return
        initialized = true
        intent {
            reduce { state.copy(characterId = characterId) }
        }
        loadCharacter(characterId)
    }

    fun handleIntent(intent: EditCharacterIntent) {
        when (intent) {
            is EditCharacterIntent.ChangeLastName -> intent {
                reduce {
                    state.copy(lastName = intent.value.filterNot { it.isWhitespace() }.take(2))
                }
            }
            is EditCharacterIntent.ChangeFirstName -> intent {
                reduce {
                    state.copy(firstName = intent.value.filterNot { it.isWhitespace() }.take(5))
                }
            }
            is EditCharacterIntent.ChangeAge -> intent {
                reduce { state.copy(age = intent.value.filter { it.isDigit() }.take(2)) }
            }
            is EditCharacterIntent.SelectJob -> intent {
                reduce { state.copy(jobLabel = intent.label) }
            }
            is EditCharacterIntent.SelectMbti -> intent {
                reduce { state.copy(mbti = intent.mbti) }
            }
            is EditCharacterIntent.ChangeSpiceLevel -> intent {
                reduce { state.copy(spiceLevel = intent.value.coerceIn(0, 100)) }
            }
            is EditCharacterIntent.ToggleTrait -> toggleTrait(intent.keyword)
            is EditCharacterIntent.ClickEditPhoto -> openImagePicker()
            is EditCharacterIntent.ChangePickerGender -> changePickerGender(intent.gender)
            is EditCharacterIntent.SelectPickerImage -> intent {
                reduce {
                    state.copy(
                        selectedImageId = intent.option.id,
                        draftImageUrl = intent.option.imageUrl,
                    )
                }
            }
            is EditCharacterIntent.ConfirmPickerImage -> confirmPickerImage()
            is EditCharacterIntent.DismissImagePicker -> intent {
                reduce { state.copy(showImagePicker = false) }
            }
            is EditCharacterIntent.ClickComplete -> complete()
            is EditCharacterIntent.ClickBack -> intent {
                postSideEffect(EditCharacterSideEffect.NavigateBack)
            }
        }
    }

    private fun loadCharacter(characterId: Long) = intent {
        reduce { state.copy(loadStatus = LoadStatus.Loading, characterId = characterId) }
        aiCharacterRepository.getCharacterDetail(characterId)
            .onSuccess { detail ->
                val gender = detail.gender.toProfileImageGender()
                val jobLabel = EditCharacterJob.fromApi(detail.job)?.label.orEmpty()
                reduce {
                    state.copy(
                        loadStatus = LoadStatus.Idle,
                        imageUrl = detail.imageUrl,
                        gender = gender,
                        lastName = detail.lastName,
                        firstName = detail.firstName,
                        age = detail.age?.toString().orEmpty(),
                        jobLabel = jobLabel,
                        mbti = detail.mbti.orEmpty(),
                        spiceLevel = detail.spiceLevel,
                        selectedTraitKeywords = detail.traits
                            .sortedBy { it.priority }
                            .map { it.code },
                        preferTime = detail.preferTime,
                        speechStyle = detail.speechStyle,
                        relationshipStage = detail.relationshipStage,
                        pickerGender = gender,
                        draftGender = gender,
                        draftImageUrl = detail.imageUrl,
                    )
                }
                loadPresetImages(gender)
            }
            .onFailure { error ->
                if (error is CancellationException) throw error
                reduce {
                    state.copy(
                        loadStatus = LoadStatus.Error(
                            error.toUserMessage(default = "캐릭터 정보를 불러오지 못했습니다."),
                        ),
                    )
                }
                postSideEffect(
                    EditCharacterSideEffect.ShowMessage(
                        error.toUserMessage(default = "캐릭터 정보를 불러오지 못했습니다."),
                    ),
                )
                postSideEffect(EditCharacterSideEffect.NavigateBack)
            }
    }

    private fun toggleTrait(keyword: String) = intent {
        val current = state.selectedTraitKeywords
        val next = when {
            keyword in current -> current - keyword
            current.size >= 5 -> current
            else -> current + keyword
        }
        reduce { state.copy(selectedTraitKeywords = next) }
    }

    private fun openImagePicker() = intent {
        val gender = state.gender
        reduce {
            state.copy(
                showImagePicker = true,
                pickerGender = gender,
                draftGender = gender,
                draftImageUrl = state.imageUrl,
                selectedImageId = state.pickerImages
                    .firstOrNull { it.imageUrl == state.imageUrl }
                    ?.id
                    ?: state.pickerImages.firstOrNull()?.id,
            )
        }
        loadPresetImages(gender)
    }

    private fun changePickerGender(gender: ProfileImageGender) = intent {
        reduce {
            state.copy(
                pickerGender = gender,
                draftGender = gender,
                selectedImageId = null,
                draftImageUrl = "",
            )
        }
        loadPresetImages(gender)
    }

    private fun confirmPickerImage() = intent {
        val imageUrl = state.draftImageUrl.ifBlank {
            state.pickerImages.firstOrNull()?.imageUrl.orEmpty()
        }
        if (imageUrl.isBlank()) {
            postSideEffect(EditCharacterSideEffect.ShowMessage("프로필 사진을 선택해 주세요."))
            return@intent
        }
        reduce {
            state.copy(
                imageUrl = imageUrl,
                gender = state.draftGender,
                showImagePicker = false,
            )
        }
    }

    private fun complete() = intent {
        if (!state.canComplete) return@intent
        val age = state.age.toIntOrNull() ?: return@intent
        val job = EditCharacterJob.fromLabel(state.jobLabel) ?: return@intent

        reduce { state.copy(isSaving = true) }
        aiCharacterRepository.updateCharacter(
            characterId = state.characterId,
            input = CharacterUpdateInput(
                lastName = state.lastName.trim(),
                firstName = state.firstName.trim(),
                gender = state.gender.name,
                age = age,
                job = job.apiValue,
                imageUrl = state.imageUrl,
                spiceLevel = state.spiceLevel,
                preferTime = state.preferTime,
                mbti = state.mbti,
                speechStyle = state.speechStyle,
                relationshipStage = state.relationshipStage,
                traits = state.selectedTraitKeywords.mapIndexed { index, code ->
                    CharacterTraitDetail(code = code, priority = index + 1)
                },
            ),
        ).onSuccess {
            reduce { state.copy(isSaving = false) }
            postSideEffect(EditCharacterSideEffect.NavigateBack)
        }.onFailure { error ->
            if (error is CancellationException) throw error
            reduce { state.copy(isSaving = false) }
            val code = (error as? AppException.Business)?.code
            if (code == "CHARACTER400_7") {
                postSideEffect(EditCharacterSideEffect.ShowEditLimitExceeded)
            } else {
                postSideEffect(
                    EditCharacterSideEffect.ShowMessage(
                        error.toUserMessage(default = "이상형 정보 저장에 실패했습니다."),
                    ),
                )
            }
        }
    }

    private fun loadPresetImages(gender: ProfileImageGender) = intent {
        val alreadyLoaded = when (gender) {
            ProfileImageGender.MALE -> state.isMalePresetsLoaded
            ProfileImageGender.FEMALE -> state.isFemalePresetsLoaded
        }
        if (alreadyLoaded) return@intent

        onboardingRepository.getPresetImages(gender.name)
            .onSuccess { images ->
                val options = images.map {
                    ProfileImageOption(id = it.id.toString(), imageUrl = it.imageUrl)
                }
                reduce {
                    when (gender) {
                        ProfileImageGender.MALE -> state.copy(
                            malePresetImages = options,
                            isMalePresetsLoaded = true,
                        )
                        ProfileImageGender.FEMALE -> state.copy(
                            femalePresetImages = options,
                            isFemalePresetsLoaded = true,
                        )
                    }
                }
            }
            .onFailure { error ->
                if (error is CancellationException) throw error
            }
    }
}

private fun String.toProfileImageGender(): ProfileImageGender =
    when (uppercase()) {
        "FEMALE" -> ProfileImageGender.FEMALE
        else -> ProfileImageGender.MALE
    }

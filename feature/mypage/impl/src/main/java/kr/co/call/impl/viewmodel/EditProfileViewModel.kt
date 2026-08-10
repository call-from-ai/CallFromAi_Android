package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject
import kr.co.call.designsystem.component.profileimage.ProfileImageGender
import kr.co.call.designsystem.component.profileimage.ProfileImageOption
import kr.co.call.domain.exception.toUserMessage
import kr.co.call.domain.model.mypage.MemberProfileUpdate
import kr.co.call.domain.repository.MyPageRepository
import kr.co.call.domain.repository.OnboardingRepository
import kr.co.call.domain.util.LoadStatus
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository,
    private val onboardingRepository: OnboardingRepository,
) : ViewModel(), ContainerHost<EditProfileState, EditProfileSideEffect> {

    override val container: Container<EditProfileState, EditProfileSideEffect> = container(
        initialState = EditProfileState(),
    ) {
        loadProfile()
    }

    fun handleIntent(intent: EditProfileIntent) {
        when (intent) {
            is EditProfileIntent.ChangeLastName -> changeLastName(intent.value)
            is EditProfileIntent.ChangeFirstName -> changeFirstName(intent.value)
            is EditProfileIntent.SelectBirth -> selectBirth(intent.date)
            is EditProfileIntent.SelectJob -> selectJob(intent.job)
            is EditProfileIntent.SelectMbti -> selectMbti(intent.mbti)
            is EditProfileIntent.ClickEditPhoto -> openImagePicker()
            is EditProfileIntent.ChangePickerGender -> changePickerGender(intent.gender)
            is EditProfileIntent.SelectPickerImage -> selectPickerImage(intent.option)
            is EditProfileIntent.ConfirmPickerImage -> confirmPickerImage()
            is EditProfileIntent.DismissImagePicker -> dismissImagePicker()
            is EditProfileIntent.ClickComplete -> complete()
            is EditProfileIntent.ClickBack -> navigateBack()
        }
    }

    private fun loadProfile() = intent {
        reduce { state.copy(loadStatus = LoadStatus.Loading) }
        myPageRepository.getMyProfile()
            .onSuccess { profile ->
                val gender = profile.gender.toProfileImageGender()
                reduce {
                    state.copy(
                        loadStatus = LoadStatus.Idle,
                        imageUrl = profile.profileImageUrl,
                        lastName = profile.lastName,
                        firstName = profile.firstName,
                        birth = profile.birth.toLocalDateOrNull(),
                        job = EditProfileJob.fromApi(profile.job),
                        mbti = profile.mbti,
                        gender = gender,
                        pickerGender = gender,
                        draftGender = gender,
                        draftImageUrl = profile.profileImageUrl,
                    )
                }
                loadPresetImages(gender)
            }
            .onFailure { error ->
                if (error is CancellationException) throw error
                reduce {
                    state.copy(
                        loadStatus = LoadStatus.Error(
                            error.toUserMessage(default = "프로필을 불러오지 못했습니다."),
                        ),
                    )
                }
            }
    }

    private fun changeLastName(value: String) = intent {
        reduce { state.copy(lastName = value) }
    }

    private fun changeFirstName(value: String) = intent {
        reduce { state.copy(firstName = value) }
    }

    private fun selectBirth(date: LocalDate) = intent {
        reduce { state.copy(birth = date) }
    }

    private fun selectJob(job: EditProfileJob) = intent {
        reduce { state.copy(job = job) }
    }

    private fun selectMbti(mbti: String) = intent {
        reduce { state.copy(mbti = mbti) }
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

    private fun selectPickerImage(option: ProfileImageOption) = intent {
        reduce {
            state.copy(
                selectedImageId = option.id,
                draftImageUrl = option.imageUrl,
            )
        }
    }

    private fun confirmPickerImage() = intent {
        val imageUrl = state.draftImageUrl.ifBlank {
            state.pickerImages.firstOrNull()?.imageUrl.orEmpty()
        }
        if (imageUrl.isBlank()) {
            postSideEffect(EditProfileSideEffect.ShowMessage("프로필 사진을 선택해 주세요."))
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

    private fun dismissImagePicker() = intent {
        reduce { state.copy(showImagePicker = false) }
    }

    private fun complete() = intent {
        if (!state.canComplete) return@intent

        val birth = state.birth ?: return@intent
        val job = state.job ?: return@intent
        val mbti = state.mbti ?: return@intent

        reduce { state.copy(isSaving = true) }

        myPageRepository.updateMyProfile(
            MemberProfileUpdate(
                lastName = state.lastName.trim(),
                firstName = state.firstName.trim(),
                imageUrl = state.imageUrl,
                gender = state.gender.name,
                birth = birth.toString(),
                mbti = mbti,
                job = job.apiValue,
            ),
        ).onSuccess { profile ->
            reduce { state.copy(isSaving = false) }
            // 저장 결과를 상위 화면에 전달
            postSideEffect(
                EditProfileSideEffect.ProfileSaved(
                    nickname = profile.nickname.ifBlank {
                        profile.lastName + profile.firstName
                    },
                    profileImageUrl = profile.profileImageUrl,
                ),
            )
            postSideEffect(EditProfileSideEffect.NavigateBack)
        }.onFailure { error ->
            if (error is CancellationException) throw error
            reduce { state.copy(isSaving = false) }
            postSideEffect(
                EditProfileSideEffect.ShowMessage(
                    error.toUserMessage(default = "프로필 저장에 실패했습니다."),
                ),
            )
        }
    }

    private fun navigateBack() = intent {
        postSideEffect(EditProfileSideEffect.NavigateBack)
    }

    private fun loadPresetImages(gender: ProfileImageGender) = intent {
        val alreadyLoaded = when (gender) {
            ProfileImageGender.MALE -> state.isMalePresetsLoaded
            ProfileImageGender.FEMALE -> state.isFemalePresetsLoaded
        }
        if (alreadyLoaded) {
            syncSelectedImageForGender(gender)
            return@intent
        }

        onboardingRepository.getPresetImages(gender.name)
            .onSuccess { images ->
                val options = images.map { preset ->
                    ProfileImageOption(
                        id = preset.id.toString(),
                        imageUrl = preset.imageUrl,
                    )
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
                syncSelectedImageForGender(gender)
            }
            .onFailure { error ->
                if (error is CancellationException) throw error
                if (state.showImagePicker) {
                    postSideEffect(
                        EditProfileSideEffect.ShowMessage(
                            error.toUserMessage(default = "프로필 사진을 불러오지 못했습니다."),
                        ),
                    )
                }
            }
    }

    private fun syncSelectedImageForGender(gender: ProfileImageGender) = intent {
        if (state.pickerGender != gender) return@intent
        val images = when (gender) {
            ProfileImageGender.MALE -> state.malePresetImages
            ProfileImageGender.FEMALE -> state.femalePresetImages
        }
        if (images.isEmpty()) return@intent

        val matched = images.firstOrNull { it.imageUrl == state.imageUrl }
            ?: images.firstOrNull { it.id == state.selectedImageId }
            ?: images.first()

        reduce {
            state.copy(
                selectedImageId = matched.id,
                draftImageUrl = matched.imageUrl,
                draftGender = gender,
            )
        }
    }
}

private fun String?.toProfileImageGender(): ProfileImageGender =
    when (this?.uppercase()) {
        "FEMALE" -> ProfileImageGender.FEMALE
        else -> ProfileImageGender.MALE
    }

private fun String?.toLocalDateOrNull(): LocalDate? {
    if (this.isNullOrBlank()) return null
    return try {
        LocalDate.parse(this)
    } catch (_: DateTimeParseException) {
        null
    }
}


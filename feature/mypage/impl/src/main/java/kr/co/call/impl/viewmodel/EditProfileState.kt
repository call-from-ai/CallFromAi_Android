package kr.co.call.impl.viewmodel

import java.time.LocalDate
import kr.co.call.designsystem.component.profileimage.ProfileImageGender
import kr.co.call.designsystem.component.profileimage.ProfileImageOption
import kr.co.call.domain.util.LoadStatus

data class EditProfileState(
    val loadStatus: LoadStatus = LoadStatus.Idle,
    val isSaving: Boolean = false,
    val imageUrl: String = "",
    val lastName: String = "",
    val firstName: String = "",
    val birth: LocalDate? = null,
    val job: EditProfileJob? = null,
    val mbti: String? = null,
    val gender: ProfileImageGender = ProfileImageGender.MALE,
    val showImagePicker: Boolean = false,
    val malePresetImages: List<ProfileImageOption> = emptyList(),
    val femalePresetImages: List<ProfileImageOption> = emptyList(),
    val isMalePresetsLoaded: Boolean = false,
    val isFemalePresetsLoaded: Boolean = false,
    val pickerGender: ProfileImageGender = ProfileImageGender.MALE,
    val selectedImageId: String? = null,
    val draftImageUrl: String = "",
    val draftGender: ProfileImageGender = ProfileImageGender.MALE,
) {
    val displayName: String
        get() = lastName + firstName

    val pickerImages: List<ProfileImageOption>
        get() = when (pickerGender) {
            ProfileImageGender.MALE -> malePresetImages
            ProfileImageGender.FEMALE -> femalePresetImages
        }

    val isPickerLoading: Boolean
        get() = pickerImages.isEmpty() &&
            when (pickerGender) {
                ProfileImageGender.MALE -> !isMalePresetsLoaded
                ProfileImageGender.FEMALE -> !isFemalePresetsLoaded
            }

    val isFormValid: Boolean
        get() = lastName.isNotBlank() &&
            firstName.isNotBlank() &&
            birth != null &&
            job != null &&
            !mbti.isNullOrBlank() &&
            imageUrl.isNotBlank()

    val canComplete: Boolean
        get() = isFormValid && !isSaving && loadStatus !is LoadStatus.Loading
}

enum class EditProfileJob(
    val label: String,
    val apiValue: String,
) {
    UNIVERSITY_STUDENT("대학생", "UNIVERSITY_STUDENT"),
    EMPLOYEE("직장인", "EMPLOYEE"),
    OTHER("기타", "OTHER"),
    ;

    companion object {
        fun fromApi(value: String?): EditProfileJob? =
            entries.firstOrNull { it.apiValue.equals(value, ignoreCase = true) }
    }
}

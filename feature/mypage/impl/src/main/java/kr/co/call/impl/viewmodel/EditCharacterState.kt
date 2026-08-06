package kr.co.call.impl.viewmodel

import kr.co.call.designsystem.component.profileimage.ProfileImageGender
import kr.co.call.designsystem.component.profileimage.ProfileImageOption
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.viewmodel.model.Trait

data class EditCharacterState(
    val characterId: Long = 0L,
    val loadStatus: LoadStatus = LoadStatus.Idle,
    val isSaving: Boolean = false,
    val imageUrl: String = "",
    val gender: ProfileImageGender = ProfileImageGender.MALE,
    val lastName: String = "",
    val firstName: String = "",
    val age: String = "",
    val jobLabel: String = "",
    val mbti: String = "",
    val spiceLevel: Int = 50,
    val selectedTraitKeywords: List<String> = emptyList(),
    val preferTime: String = "",
    val speechStyle: String = "",
    val relationshipStage: String = "",
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

    val selectedTraits: List<Trait>
        get() = selectedTraitKeywords.mapNotNull(Trait::fromKeyword)

    val isFormValid: Boolean
        get() = lastName.isNotBlank() &&
            firstName.isNotBlank() &&
            age.toIntOrNull() != null &&
            jobLabel.isNotBlank() &&
            imageUrl.isNotBlank() &&
            selectedTraitKeywords.isNotEmpty()

    val canComplete: Boolean
        get() = isFormValid && !isSaving && loadStatus !is LoadStatus.Loading
}

enum class EditCharacterJob(
    val label: String,
    val apiValue: String,
) {
    UNIVERSITY_STUDENT("대학생", "UNIVERSITY_STUDENT"),
    EMPLOYEE("직장인", "EMPLOYEE"),
    OTHER("기타", "OTHER"),
    ;

    companion object {
        fun fromApi(value: String?): EditCharacterJob? =
            entries.firstOrNull { it.apiValue.equals(value, ignoreCase = true) }
                ?: entries.firstOrNull { it.label == value }

        fun fromLabel(label: String): EditCharacterJob? =
            entries.firstOrNull { it.label == label }
    }
}

package kr.co.call.impl.viewmodel

import kr.co.call.designsystem.component.profileimage.ProfileImageGender
import kr.co.call.designsystem.component.profileimage.ProfileImageOption

sealed interface EditCharacterIntent {
    data class ChangeLastName(val value: String) : EditCharacterIntent
    data class ChangeFirstName(val value: String) : EditCharacterIntent
    data class ChangeAge(val value: String) : EditCharacterIntent
    data class SelectJob(val label: String) : EditCharacterIntent
    data class SelectMbti(val mbti: String) : EditCharacterIntent
    data class ChangeSpiceLevel(val value: Int) : EditCharacterIntent
    data class ToggleTrait(val keyword: String) : EditCharacterIntent

    data object ClickEditPhoto : EditCharacterIntent
    data class ChangePickerGender(val gender: ProfileImageGender) : EditCharacterIntent
    data class SelectPickerImage(val option: ProfileImageOption) : EditCharacterIntent
    data object ConfirmPickerImage : EditCharacterIntent
    data object DismissImagePicker : EditCharacterIntent

    data object ClickComplete : EditCharacterIntent
    data object ClickBack : EditCharacterIntent
}

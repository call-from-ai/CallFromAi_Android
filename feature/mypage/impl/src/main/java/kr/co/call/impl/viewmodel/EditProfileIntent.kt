package kr.co.call.impl.viewmodel

import java.time.LocalDate
import kr.co.call.designsystem.component.profileimage.ProfileImageGender
import kr.co.call.designsystem.component.profileimage.ProfileImageOption

sealed interface EditProfileIntent {
    data class ChangeLastName(val value: String) : EditProfileIntent
    data class ChangeFirstName(val value: String) : EditProfileIntent

    data class SelectBirth(val date: LocalDate) : EditProfileIntent

    data class SelectJob(val job: EditProfileJob) : EditProfileIntent
    data class SelectMbti(val mbti: String) : EditProfileIntent

    data object ClickEditPhoto : EditProfileIntent
    data class ChangePickerGender(val gender: ProfileImageGender) : EditProfileIntent
    data class SelectPickerImage(val option: ProfileImageOption) : EditProfileIntent
    data object ConfirmPickerImage : EditProfileIntent
    data object DismissImagePicker : EditProfileIntent

    data object ClickComplete : EditProfileIntent
    data object ClickBack : EditProfileIntent
}

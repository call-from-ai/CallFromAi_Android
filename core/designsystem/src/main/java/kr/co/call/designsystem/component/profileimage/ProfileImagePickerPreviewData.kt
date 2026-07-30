package kr.co.call.designsystem.component.profileimage

/**
 * designsystem Preview 전용 샘플 데이터
 */
internal object ProfileImagePickerPreviewData {

    val maleImages: List<ProfileImageOption> = listOf(
        ProfileImageOption(id = "1", imageUrl = ""),
        ProfileImageOption(id = "2", imageUrl = ""),
        ProfileImageOption(id = "3", imageUrl = ""),
        ProfileImageOption(id = "4", imageUrl = ""),
        ProfileImageOption(id = "5", imageUrl = ""),
    )

    val femaleImages: List<ProfileImageOption> = listOf(
        ProfileImageOption(id = "6", imageUrl = ""),
        ProfileImageOption(id = "7", imageUrl = ""),
        ProfileImageOption(id = "8", imageUrl = ""),
        ProfileImageOption(id = "9", imageUrl = ""),
        ProfileImageOption(id = "10", imageUrl = ""),
    )

    fun imagesFor(gender: ProfileImageGender): List<ProfileImageOption> = when (gender) {
        ProfileImageGender.MALE -> maleImages
        ProfileImageGender.FEMALE -> femaleImages
    }
}

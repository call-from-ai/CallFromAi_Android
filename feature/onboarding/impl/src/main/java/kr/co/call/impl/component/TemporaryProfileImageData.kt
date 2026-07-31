package kr.co.call.impl.component

import kr.co.call.designsystem.component.profileimage.ProfileImageGender
import kr.co.call.designsystem.component.profileimage.ProfileImageOption

//서버에서 사진 받아오기 전까지 임시로 사용
internal object TemporaryProfileImageData {

    val maleImages = List(5) { index ->
        ProfileImageOption(
            id = "temporary_male_${index + 1}",
            imageUrl = "",
        )
    }

    val femaleImages = List(5) { index ->
        ProfileImageOption(
            id = "temporary_female_${index + 1}",
            imageUrl = "",
        )
    }

    fun imagesFor(
        gender: ProfileImageGender,
    ): List<ProfileImageOption> = when (gender) {
        ProfileImageGender.MALE -> maleImages
        ProfileImageGender.FEMALE -> femaleImages
    }
}
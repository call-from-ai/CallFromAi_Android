package kr.co.call.network.dto.onboarding

import com.google.gson.annotations.SerializedName

data class CreateCharacterResponseDto(
    @SerializedName("characterId")
    val id: Long,
    @SerializedName("name")
    val name: String?,
    @SerializedName("imageUrl")
    val characterImageUrl: String?,
)
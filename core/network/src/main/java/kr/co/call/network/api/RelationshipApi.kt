package kr.co.call.network.api

import kr.co.call.network.dto.ApiResponse
import kr.co.call.network.dto.mypage.ContactPreferenceResponseDto
import kr.co.call.network.dto.mypage.ContactPreferenceUpdateRequestDto
import retrofit2.http.Body
import retrofit2.http.PATCH

interface RelationshipApi {
    @PATCH("relationships/current/contact-preference")
    suspend fun updateContactPreference(
        @Body request: ContactPreferenceUpdateRequestDto,
    ): ApiResponse<ContactPreferenceResponseDto>
}

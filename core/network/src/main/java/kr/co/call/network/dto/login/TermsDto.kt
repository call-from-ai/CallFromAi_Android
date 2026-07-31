package kr.co.call.network.dto.login

import com.google.gson.annotations.SerializedName

//개별 약관 항목
data class TermDto(
    val termId: Long,
    val title: String,
    val content: String,
    val isRequired: Boolean,
)

data class AgreeTermsRequestDto(
    val agreements: List<TermAgreementDto>,
)

data class TermAgreementDto(
    val termId:Long,
    @SerializedName("agreed")
    val isAgreed: Boolean
)
package kr.co.call.network.dto.login

//개별 약관 항목
data class TermDto(
    val termId: Long,
    val title: String,
    val content: String,
    val isRequired: Boolean,
)

data class GetTermsResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<TermDto>?,
)

data class AgreeTermsRequestDto(
    val agreements: List<TermAgreementDto>,
)

data class TermAgreementDto(
    val termId:Long,
    val isAgreed: Boolean
)

data class AgreeTermsResponseDto(
    val isSuccess:Boolean,
    val code: String,
    val message: String,
)
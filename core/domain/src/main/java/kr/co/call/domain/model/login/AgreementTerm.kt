package kr.co.call.domain.model.login

data class AgreementTerm (
    val termId: Long,
    val title: String,
    val content: String,
    val isRequired: Boolean,
)
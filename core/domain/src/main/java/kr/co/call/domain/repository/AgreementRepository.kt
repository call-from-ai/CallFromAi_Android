package kr.co.call.domain.repository

import kr.co.call.domain.model.login.AgreementTerm

interface AgreementRepository {
    suspend fun getTerms(): List<AgreementTerm>
    suspend fun agreeTerms(agreements: Map<Long, Boolean>)
}
package kr.co.call.domain.repository

import kr.co.call.domain.model.login.AgreementTerm

/**
 * 약관 목록 조회와 약관 동의 요청을 담당하는 Repository입니다.
 * 성공과 실패 결과를 Result 형태로 상위 계층에 전달합니다.
 */
interface AgreementRepository {
    suspend fun getTerms(): Result<List<AgreementTerm>>
    suspend fun agreeTerms(
        agreements: Map<Long, Boolean>
    ): Result<Unit>
}
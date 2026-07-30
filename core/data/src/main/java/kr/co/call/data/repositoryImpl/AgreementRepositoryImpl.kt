package kr.co.call.data.repositoryImpl

import javax.inject.Inject
import kr.co.call.data.util.runRepositoryCatching
import kr.co.call.domain.model.login.AgreementTerm
import kr.co.call.domain.model.login.TermAgreement
import kr.co.call.domain.repository.AgreementRepository
import kr.co.call.network.api.AgreementApi
import kr.co.call.network.dto.login.AgreeTermsRequestDto
import kr.co.call.network.dto.login.TermAgreementDto
import kr.co.call.network.util.ErrorResponseParser
import kr.co.call.network.util.safeApiCall
import kr.co.call.network.util.safeApiCallUnit

/**
 * 서버의 약관 API를 호출하고 응답 DTO를 Domain Model로 변환합니다.
 * 약관 조회 및 동의 결과를 Result 형태로 ViewModel에 전달합니다.
 */
class AgreementRepositoryImpl @Inject constructor(
    private val agreementApi: AgreementApi,
    private val errorResponseParser: ErrorResponseParser,
) : AgreementRepository {

    /**
     * 서버에서 약관 목록을 조회한 뒤
     * 화면에서 사용할 AgreementTerm 목록으로 변환합니다.
     */
    override suspend fun getTerms(): Result<List<AgreementTerm>> =
        runRepositoryCatching {
            safeApiCall(errorResponseParser) {
                agreementApi.getTerms()
            }.map { dto ->
                AgreementTerm(
                    termId = dto.termId,
                    title = dto.title,
                    content = dto.content,
                    isRequired = dto.isRequired,
                )
            }
        }

    /**
     * 사용자가 선택한 약관별 동의 여부를 서버에 전달합니다.
     */
    override suspend fun agreeTerms(
        agreements: List<TermAgreement>,
    ): Result<Unit> =
        runRepositoryCatching {
            safeApiCallUnit(errorResponseParser) {
                agreementApi.agreeTerms(
                    request = AgreeTermsRequestDto(
                        agreements = agreements.map { agreement ->
                            TermAgreementDto(
                                termId = agreement.termId,
                                isAgreed = agreement.isAgreed,
                            )
                        },
                    ),
                )
            }
        }
}
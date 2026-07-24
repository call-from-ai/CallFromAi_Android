package kr.co.call.data.repositoryImpl

import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kr.co.call.domain.model.login.AgreementTerm
import kr.co.call.domain.repository.AgreementRepository
import kr.co.call.network.api.AgreementApi
import kr.co.call.network.dto.login.AgreeTermsRequestDto
import kr.co.call.network.dto.login.TermAgreementDto

/**
 * 서버의 약관 API를 호출하고 응답 DTO를 Domain Model로 변환합니다.
 * 약관 조회 및 동의 결과를 Result 형태로 ViewModel에 전달합니다.
 */
class AgreementRepositoryImpl @Inject constructor(
    private val agreementApi: AgreementApi,
) : AgreementRepository {

    /**
     * 서버에서 약관 목록을 조회한 뒤
     * 화면에서 사용할 AgreementTerm 목록으로 변환합니다.
     */
    override suspend fun getTerms(): Result<List<AgreementTerm>> {
        return try {
            val response = agreementApi.getTerms()

            // 서버가 약관 조회 실패 응답을 반환한 경우
            if (!response.isSuccess) {
                throw IllegalStateException(
                    response.message.ifBlank {
                        "약관을 불러오지 못했습니다."
                    },
                )
            }

            // 성공 응답이지만 약관 목록이 없는 경우
            val terms = response.result
                ?: throw IllegalStateException(
                    "약관 응답에 약관 목록이 없습니다.",
                )

            // 서버 DTO를 화면에서 사용할 Domain Model로 변환
            val agreementTerms = terms.map { dto ->
                AgreementTerm(
                    termId = dto.termId,
                    title = dto.title,
                    content = dto.content,
                    isRequired = dto.isRequired,
                )
            }

            Result.success(agreementTerms)
        } catch (error: CancellationException) {
            // 코루틴 취소는 일반적인 실패로 처리하지 않음
            throw error
        } catch (error: Exception) {
            // 서버 응답 오류나 네트워크 오류를 실패 결과로 전달
            Result.failure(error)
        }
    }

    /**
     * 사용자가 선택한 약관별 동의 여부를 서버에 전달합니다.
     */
    override suspend fun agreeTerms(
        agreements: Map<Long, Boolean>,
    ): Result<Unit> {
        return try {
            val response = agreementApi.agreeTerms(
                request = AgreeTermsRequestDto(
                    agreements = agreements.map { (termId, isAgreed) ->
                        TermAgreementDto(
                            termId = termId,
                            isAgreed = isAgreed,
                        )
                    },
                ),
            )

            // 서버가 약관 동의 실패 응답을 반환한 경우
            if (!response.isSuccess) {
                throw IllegalStateException(
                    response.message.ifBlank {
                        "약관 동의에 실패했습니다."
                    },
                )
            }

            Result.success(Unit)
        } catch (error: CancellationException) {
            // 코루틴 취소는 일반적인 실패로 처리하지 않음
            throw error
        } catch (error: Exception) {
            // 서버 응답 오류나 네트워크 오류를 실패 결과로 전달
            Result.failure(error)
        }
    }
}
package kr.co.call.data.repositoryImpl

import kr.co.call.domain.model.login.AgreementTerm
import kr.co.call.domain.repository.AgreementRepository
import kr.co.call.network.api.AgreementApi
import kr.co.call.network.dto.login.AgreeTermsRequestDto
import kr.co.call.network.dto.login.TermAgreementDto
import javax.inject.Inject

class AgreementRepositoryImpl @Inject constructor(
    private val agreementApi: AgreementApi,
): AgreementRepository {
    override suspend fun getTerms():List<AgreementTerm>{
        val response=agreementApi.getTerms()
        if (!response.isSuccess){
            throw IllegalStateException(response.message)
        }
        return response.result.orEmpty().map {dto ->
            AgreementTerm(
                termId=dto.termId,
                title=dto.title,
                content=dto.content,
                isRequired=dto.isRequired,
            )
        }
    }

    override suspend fun agreeTerms(agreements: Map<Long,Boolean>){
        val response=agreementApi.agreeTerms(
            request = AgreeTermsRequestDto(
                agreements=agreements.map{(termId, isAgreed)->
                    TermAgreementDto(termId = termId, isAgreed = isAgreed)
                },
            ),
        )
        if (!response.isSuccess){
            throw IllegalStateException(response.message)
        }
    }
}
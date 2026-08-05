package kr.co.call.data.repositoryImpl

import kr.co.call.data.mapper.toDomain
import kr.co.call.data.mapper.toRequestDto
import kr.co.call.data.util.safeApiResult
import javax.inject.Inject
import kr.co.call.data.util.safeApiResultUnit
import kr.co.call.datastore.TokenDataStore
import kr.co.call.domain.model.onboarding.CharacterOnboardingInput
import kr.co.call.domain.model.onboarding.CreatedCharacter
import kr.co.call.domain.model.onboarding.MemberOnboardingInput
import kr.co.call.domain.model.onboarding.PresetImage
import kr.co.call.domain.repository.OnboardingRepository
import kr.co.call.network.api.AICharacterApi
import kr.co.call.network.api.MyPageApi
import kr.co.call.network.api.PresetImageApi
import kr.co.call.network.util.ErrorResponseParser
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

class OnboardingRepositoryImpl @Inject constructor(
    private val myPageApi: MyPageApi,
    private val aiCharacterApi: AICharacterApi,
    private val presetImageApi: PresetImageApi,
    private val errorResponseParser: ErrorResponseParser,
    private val tokenDataStore: TokenDataStore,
) : OnboardingRepository {

    override suspend fun submitMemberOnboarding(
        submission: MemberOnboardingInput,
    ): Result<Unit> =
        safeApiResultUnit(errorResponseParser) {
            myPageApi.updateMember(
                request = submission.toRequestDto(),
            )
        }

    override suspend fun submitCharacterOnboarding(
        submission: CharacterOnboardingInput,
    ): Result<CreatedCharacter> {
        val createResult=
        safeApiResult(errorResponseParser) {
            aiCharacterApi.createCharacter(
                request = submission.toRequestDto(),
            )
        }.mapCatching{response ->
            response.toDomain()
        }
    createResult.onSuccess { character ->
        try {
            tokenDataStore.setNeedsOnboarding(false)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Throwable) {
            Timber.e(
                error,
                "온보딩 상태 저장 실패: characterId=%d",
                character.id,
            )
        }
    }
        return createResult
    }

    override suspend fun getPresetImages(
        gender: String,
    ):Result<List<PresetImage>> =
        safeApiResult(errorResponseParser){
            presetImageApi.getPresetImages(gender)
        }.map{ responses ->
            responses.map{it.toDomain()}
        }
}

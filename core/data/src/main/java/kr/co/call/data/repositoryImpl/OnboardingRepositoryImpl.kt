package kr.co.call.data.repositoryImpl

import kr.co.call.data.util.safeApiResult
import javax.inject.Inject
import kr.co.call.data.util.safeApiResultUnit
import kr.co.call.domain.model.onboarding.CharacterOnboardingInput
import kr.co.call.domain.model.onboarding.MemberOnboardingInput
import kr.co.call.domain.model.onboarding.PresetImage
import kr.co.call.domain.repository.OnboardingRepository
import kr.co.call.network.api.AICharacterApi
import kr.co.call.network.api.MyPageApi
import kr.co.call.network.api.PresetImageApi
import kr.co.call.network.dto.onboarding.CharacterTraitRequestDto
import kr.co.call.network.dto.onboarding.CreateCharacterRequestDto
import kr.co.call.network.dto.onboarding.UpdateMemberRequestDto
import kr.co.call.network.util.ErrorResponseParser

class OnboardingRepositoryImpl @Inject constructor(
    private val myPageApi: MyPageApi,
    private val aiCharacterApi: AICharacterApi,
    private val presetImageApi: PresetImageApi,
    private val errorResponseParser: ErrorResponseParser,
) : OnboardingRepository {

    override suspend fun submitMemberOnboarding(
        submission: MemberOnboardingInput,
    ): Result<Unit> =
        safeApiResultUnit(errorResponseParser) {
            myPageApi.updateMember(
                request = UpdateMemberRequestDto(
                    lastName = submission.lastName,
                    firstName = submission.firstName,
                    imageUrl = submission.imageUrl,
                    gender = submission.gender,
                    birth = submission.birth,
                    mbti = submission.mbti,
                    job = submission.job,
                ),
            )
        }

    override suspend fun submitCharacterOnboarding(
        submission: CharacterOnboardingInput,
    ): Result<Unit> =
        safeApiResultUnit(errorResponseParser) {
            aiCharacterApi.createCharacter(
                request = CreateCharacterRequestDto(
                    lastName = submission.lastName,
                    firstName = submission.firstName,
                    gender = submission.gender,
                    age = submission.age,
                    job = submission.job,
                    imageUrl = submission.imageUrl,
                    spiceLevel = submission.spiceLevel,
                    preferTime = submission.preferTime,
                    mbti = submission.mbti,
                    speechStyle = submission.speechStyle,
                    relationshipStage =
                        submission.relationshipStage,
                    traits = submission.traits.map { trait ->
                        CharacterTraitRequestDto(
                            trait = trait.trait,
                            priority = trait.priority,
                        )
                    },
                ),
            )
        }

    override suspend fun getPresetImages(
        gender: String,
    ):Result<List<PresetImage>> =
        safeApiResult(errorResponseParser){
            presetImageApi.getPresetImages(gender)
        }.map{ images ->
            images.map {dto->
                PresetImage(
                    id=dto.presetImageId,
                    imageUrl=dto.imageUrl,
                )
            }
        }
}

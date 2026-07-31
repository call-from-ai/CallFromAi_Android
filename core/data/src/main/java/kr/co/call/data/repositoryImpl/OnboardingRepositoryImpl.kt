package kr.co.call.data.repositoryImpl

import javax.inject.Inject
import kr.co.call.data.util.runRepositoryCatching
import kr.co.call.domain.model.onboarding.OnboardingInput
import kr.co.call.domain.repository.OnboardingRepository
import kr.co.call.network.api.AICharacterApi
import kr.co.call.network.api.MyPageApi
import kr.co.call.network.dto.onboarding.CharacterTraitRequestDto
import kr.co.call.network.dto.onboarding.CreateCharacterRequestDto
import kr.co.call.network.dto.onboarding.UpdateMemberRequestDto
import kr.co.call.network.util.ErrorResponseParser
import kr.co.call.network.util.safeApiCallUnit

class OnboardingRepositoryImpl @Inject constructor(
    private val myPageApi: MyPageApi,
    private val aiCharacterApi: AICharacterApi,
    private val errorResponseParser: ErrorResponseParser,
) : OnboardingRepository {

    override suspend fun submitOnboarding(
        submission: OnboardingInput,
    ): Result<Unit> =
        runRepositoryCatching {
            // 1. 사용자 정보 저장
            safeApiCallUnit(errorResponseParser) {
                myPageApi.updateMember(
                    request = UpdateMemberRequestDto(
                        lastName = submission.member.lastName,
                        firstName = submission.member.firstName,
                        imageUrl = submission.member.imageUrl,
                        gender = submission.member.gender,
                        birth = submission.member.birth,
                        mbti = submission.member.mbti,
                        job = submission.member.job,
                    ),
                )
            }

            // 2. 캐릭터 생성
            safeApiCallUnit(errorResponseParser) {
                aiCharacterApi.createCharacter(
                    request = CreateCharacterRequestDto(
                        lastName = submission.character.lastName,
                        firstName = submission.character.firstName,
                        gender = submission.character.gender,
                        age = submission.character.age,
                        job = submission.character.job,
                        imageUrl = submission.character.imageUrl,
                        spiceLevel = submission.character.spiceLevel,
                        preferTime = submission.character.preferTime,
                        mbti = submission.character.mbti,
                        speechStyle = submission.character.speechStyle,
                        relationshipStage =
                            submission.character.relationshipStage,
                        traits = submission.character.traits.map { trait ->
                            CharacterTraitRequestDto(
                                trait = trait.trait,
                                priority = trait.priority,
                            )
                        },
                    ),
                )
            }
        }
}

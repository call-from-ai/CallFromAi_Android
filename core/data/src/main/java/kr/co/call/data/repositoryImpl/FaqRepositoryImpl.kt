package kr.co.call.data.repositoryImpl

import kr.co.call.domain.model.mypage.FaqCategory
import kr.co.call.domain.model.mypage.FaqItem
import kr.co.call.domain.repository.FaqRepository
import javax.inject.Inject

class FaqRepositoryImpl @Inject constructor() : FaqRepository {

    override suspend fun getFaqItems(): Map<FaqCategory, List<FaqItem>> {
        return mapOf(
            FaqCategory.CHARACTER to listOf(
                FaqItem(
                    "AI 캐릭터는 여러 명 만들 수 있나요?",
                    "새로운 캐릭터를 만든 후 24시간이 지난 후, 최초 1회 변경은 무료로 가능하며, 추가 캐릭터 생성 원할 시 캐릭터 변경권을 따로 구매하여 변경하실 수 있습니다."
                ),
                FaqItem(
                    "채팅방에서 나가면 캐릭터도 함께 삭제되나요?",
                    "채팅방에서 나가도 캐릭터는 삭제되지 않습니다. 캐릭터 삭제는 마이페이지>캐릭터 관리>캐릭터 영구 삭제하기 버튼을 통해 가능합니다."
                ),
                FaqItem(
                    "중간에 캐릭터의 이름이나 설정을 변경할 수도 있나요?",
                    "네, 마이페이지>이상형 정보 수정에서 이름, 프로필 이미지, 성별, 직업 등의 정보를 변경할 수 있습니다."
                ),
                FaqItem(
                    "새로운 캐릭터를 만들려면 어떻게 하나요?",
                    "마이페이지>캐릭터 관리>캐릭터 추가하기 버튼을 눌러 새로운 캐릭터를 생성할 수 있습니다."
                ),
            ),
            FaqCategory.CALL_CHAT to listOf(
                FaqItem(
                    "전화를 놓치면 다시 받을 수 있나요?",
                    "네, 전화를 받지 못한 경우, 홈화면>지난 알림에서 부재중임이 표시되며, 다시 전화하기 버튼을 통해 통화를 시작할 수 있습니다. 다만, 이미 통화 가능 횟수를 모두 사용한 경우에는 다음날 다시 이용하실 수 있습니다."
                ),
                FaqItem(
                    "통화 시간은 직접 설정할 수 있나요?",
                    "네, 마이페이지>프로필>전화 오는 시간에서 AI에게서 먼저 전화가 걸려오는 시간대를 직접 설정할 수 있습니다."
                ),
                FaqItem(
                    "통화에서 말한 내용이 채팅에서도 이어질 수 있나요?",
                    "네, AI는 통화와 채팅의 대화 맥락을 함께 기억하여 자연스럽게 대화를 이어나갑니다."
                ),
                FaqItem(
                    "하루 통화 횟수에 제한이 있나요?",
                    "하루에 최대 1회 통화를 이용할 수 있으며 추가 통화를 원하실 경우 플랜을 업그레이드 하시거나 통화 이용권을 구매하실 수 있습니다."
                ),
            ),
            FaqCategory.PLAN_PAYMENT to emptyList(),
            FaqCategory.ACCOUNT_SETTING to emptyList(),
        )
    }
}
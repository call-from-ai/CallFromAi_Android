package kr.co.call.data.repositoryImpl

import kr.co.call.domain.model.mypage.FaqCategory
import kr.co.call.domain.model.mypage.FaqItem
import kr.co.call.domain.repository.FaqRepository
import javax.inject.Inject

class FaqRepositoryImpl @Inject constructor() : FaqRepository {

    override suspend fun getFaqItems(): Result<Map<FaqCategory, List<FaqItem>>>{
        return Result.success(mapOf(
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
            FaqCategory.PLAN_PAYMENT to listOf(
                FaqItem(
                    "플랜은 언제든 변경할 수 있나요?",
                    "네. 언제든 원하는 플랜으로 변경할 수 있습니다. 상위 플랜으로 변경하면 결제 즉시 새로운 혜택이 적용되며, 하위 플랜으로 변경하는 경우에는 현재 이용 중인 결제 기간이 종료된 후 새로운 플랜이 적용됩니다"
                ),
                FaqItem(
                    "구독을 해지하면 바로 이용이 종료되나요?",
                    "아니요. 구독을 해지하더라도 이미 결제한 이용 기간이 끝날 때까지는 서비스를 계속 이용할 수 있습니다. 이용 기간이 종료되면 다음 결제부터 자동으로 중단됩니다."
                ),
                FaqItem(
                    "결제 내역은 어디서 확인할 수 있나요?",
                    "마이페이지>내역에서 지난 결제 내역을 확인할 수 있습니다."
                ),
            ),
            FaqCategory.ACCOUNT_SETTING to listOf(
                FaqItem(
                    "알림이 오지 않아요",
                    "휴대폰의 알림 권한과 앱 내 알림 설정이 모두 활성화되어 있는지 확인해 주세요."
                ),
                FaqItem(
                    "계정을 탈퇴하면 데이터는 어떻게 되나요?",
                    "탈퇴 시 모든 캐릭터, 대화 기록 및 계정 정보가 삭제되며 복구할 수 없습니다."
                ),
                FaqItem(
                    "기기를 변경해도 기존 데이터를 사용할 수 있나요?",
                    "동일한 계정으로 로그인하면 저장된 캐릭터와 대화 기록을 이어서 이용할 수 있습니다."
                ),
                FaqItem(
                    "문의는 어디에서 할 수 있나요?",
                    "마이페이지>문의하기 혹은 채팅>전화왔어 매니저를 통해 문의를 남겨주시면 순차적으로 답변드리겠습니다."
                )
            ),
        )
        )
    }
}
package kr.co.call.impl.viewmodel.state

/**
 * 홈 화면 다이얼로그 상태
 * @property CharacterSelection 메인 연인 선택
 * @property CharacterChangeUnavailable 메인 연인 변경 제한 안내
 * @property CharacterChangeConfirmation 메인 연인 교체 확인
 */
sealed interface HomeDialogState {
    data object CharacterSelection : HomeDialogState

    data object CharacterChangeUnavailable : HomeDialogState

    data class CharacterChangeConfirmation(
        val characterId: Long,
        val characterName: String,
    ) : HomeDialogState

    data class TimeChange(
        val state: TimeChangeState,
    ) : HomeDialogState

    data class CallConfirmation(
        val characterId: Long,
        val characterName: String,
    ) : HomeDialogState

    data class NonMainCharacterCall(
        val characterName: String,
    ) : HomeDialogState
}

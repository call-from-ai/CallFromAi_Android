package kr.co.call.impl.util

import androidx.paging.compose.LazyPagingItems
import kr.co.call.impl.model.ChatItemUiModel

/**
 * DateSeparator(index k) 아래쪽(낮은 인덱스 방향 = 화면 하단)에 보이는 메시지가 있는지 확인.
 * 없으면 false를 반환해 고아 DateSeparator를 숨길 수 있도록 한다.
 *
 * @param pagingItems 페이징 아이템 목록 (newest-first, reverseLayout=true 기준)
 * @param separatorIndex 확인할 DateSeparator의 인덱스
 * @param deletedIds 낙관적 삭제 처리된 메시지 ID 집합
 */
fun shouldShowDateSeparator(
    pagingItems: LazyPagingItems<ChatItemUiModel>,
    separatorIndex: Int,
    deletedIds: Set<Long>,
): Boolean {
    var i = separatorIndex - 1
    while (i >= 0) {
        val item = pagingItems[i] ?: return true // 미로드 아이템은 보수적으로 표시 유지
        when (item) {
            is ChatItemUiModel.DateSeparator -> return false // 연속 구분선 = 사이에 메시지 없음
            is ChatItemUiModel.Message -> {
                if (item.chatMessageId !in deletedIds) return true // 보이는 메시지 발견
                // 삭제된 메시지이면 계속 탐색
            }
            else -> return true
        }
        i--
    }
    return false
}

package kr.co.call.impl.util

import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.insertSeparators
import kr.co.call.domain.model.chatting.ChatItem
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

/**
 * 채팅 아이템 목록 사이에 날짜 구분선([ChatItem.DateSeparator])을 삽입합니다.
 *
 * 두 메시지 사이의 생성 날짜([ChatItem.Message.createdTime])를 비교하여 날짜가 변경되는 지점에
 * 해당 날짜 정보를 가진 구분선을 추가합니다.
 *
 * @return 날짜 구분선이 포함된 [PagingData]
 */
fun PagingData<ChatItem>.insertDateSeparators(): PagingData<ChatItem> {
    return insertSeparators { before, after ->
        if (before == null) return@insertSeparators null

        val beforeMsg = before as? ChatItem.Message
            ?: return@insertSeparators null

        val afterMsg = after as? ChatItem.Message
            ?: return@insertSeparators null

        val beforeDate = beforeMsg.createdTime.toLocalDate()
        val afterDate = afterMsg.createdTime.toLocalDate()

        if (beforeDate != afterDate) {
            ChatItem.DateSeparator(afterDate)
        } else {
            null
        }
    }
}

package kr.co.call.data.repositoryImpl

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import kr.co.call.domain.model.chatting.ChatItem
import kr.co.call.domain.model.chatting.MessageType
import kr.co.call.domain.model.chatting.SenderType
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.milliseconds

class ChatPagingSource(
    val roomId: Long,
) : PagingSource<Long, ChatItem.Message>() {

    // TODO: API 연동 시 교체
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, ChatItem.Message> {
        return try {
            delay(500.milliseconds)

            val totalDummyCount = 100L
            val pageSize = params.loadSize.toLong()

            // cursor = before 파라미터 (이 ID 미만의 메시지 조회, null이면 최신부터)
            val beforeId = params.key ?: totalDummyCount
            val newestIdInPage = beforeId - 1
            val oldestIdInPage = maxOf(0L, newestIdInPage - pageSize + 1)

            // API 응답 시뮬레이션: oldest-first (오래된 순)
            val apiResponse = (oldestIdInPage..newestIdInPage).map { i ->
                ChatItem.Message(
                    chatMessageId = i,
                    senderType = if (i % 2L == 0L) SenderType.USER else SenderType.AI,
                    content = "더미 메시지 $i",
                    messageType = MessageType.TEXT,
                    // 10개 단위로 하루씩 과거로
                    createdTime = LocalDateTime.now().minus((totalDummyCount - 1 - i) / 10, ChronoUnit.DAYS),
                )
            }

            // reverseLayout = true 와 함께 사용하기 위해 newest-first로 변환
            val items = apiResponse.reversed()

            // reverseLayout = true 에서 오래된 메시지는 APPEND(nextKey) 방향으로 로드
            val nextKey = if (oldestIdInPage > 0L) oldestIdInPage else null

            LoadResult.Page(
                data = items,
                prevKey = null,
                nextKey = nextKey,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, ChatItem.Message>): Long? = null
}

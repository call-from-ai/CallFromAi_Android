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
            val pageSize = params.loadSize

            // cursor = 이번 페이지의 가장 최신 메시지 ID (null이면 맨 끝 = 최신)
            val newestIdInPage = params.key ?: (totalDummyCount - 1)
            val oldestIdInPage = maxOf(0L, newestIdInPage - pageSize + 1)

            val items = (oldestIdInPage..newestIdInPage).map { i ->
                ChatItem.Message(
                    chatMessageId = i,
                    senderType = if (i % 2L == 0L) SenderType.USER else SenderType.AI,
                    content = "더미 메시지 $i",
                    messageType = MessageType.TEXT,
                    // 10개 단위로 하루씩 과거로
                    createdTime = LocalDateTime.now().minus((totalDummyCount - 1 - i) / 10, ChronoUnit.DAYS),
                )
            }.reversed() // 최신이 아래에 오도록

            // 더 이전 메시지가 있으면 prevKey 세팅, 없으면 null
            val prevKey = if (oldestIdInPage > 0L) oldestIdInPage - 1 else null

            LoadResult.Page(
                data = items,
                prevKey = prevKey,
                nextKey = null, // 최신에서 시작하므로 다음 방향 없음
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, ChatItem.Message>): Long? = null
}

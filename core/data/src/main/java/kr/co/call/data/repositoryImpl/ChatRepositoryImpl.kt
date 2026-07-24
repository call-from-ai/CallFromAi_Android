package kr.co.call.data.repositoryImpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kr.co.call.domain.model.chatting.ChatItem
import kr.co.call.domain.model.chatting.ChatSummary
import kr.co.call.domain.repository.ChatRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

//TODO: 추후에 api 연동
class ChatRepositoryImpl @Inject constructor(

) : ChatRepository {
    override suspend fun getChatList(): Result<List<ChatSummary>> = runCatching {
        delay(1000.milliseconds)
        listOf(
            ChatSummary(
                chatRoomId = 2,
                name = "김민지",
                isMainCharacter = true,
                content = "오늘 저녁에 뭐해?",
                whenSubmitted = "오후 3:21",
                unReadMessageCount = "5",
                isAlarmEnabled = true,
            ),
            ChatSummary(
                chatRoomId = 3,
                name = "이서연",
                isMainCharacter = false,
                content = "사진 보내줄게!",
                whenSubmitted = "오후 1:10",
                unReadMessageCount = "0",
                isAlarmEnabled = false,
            ),
            ChatSummary(
                chatRoomId = 4,
                name = "박지우",
                isMainCharacter = true,
                content = "ㅋㅋㅋㅋ 진짜?",
                whenSubmitted = "어제",
                unReadMessageCount = "0",
                isAlarmEnabled = true,
            ),
            ChatSummary(
                chatRoomId = 5,
                name = "최예린",
                isMainCharacter = false,
                content = "나중에 연락해",
                whenSubmitted = "월요일",
                unReadMessageCount = "1",
                isAlarmEnabled = false,
            ),
        )
    }

    override suspend fun deleteChatRoom(roomId: Long): Result<Unit> = runCatching {
        delay(500.milliseconds)
    }

    override suspend fun updateAlarmSetting(roomId: Long): Result<Unit> = runCatching {
        delay(500.milliseconds)
    }

    override fun getChats(roomId: Long): Flow<PagingData<ChatItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ChatPagingSource(roomId)
            }
        ).flow.map { pagingData ->
            pagingData.insertSeparators { before, after ->
                val afterDate = after?.createdTime?.toLocalDate() ?: return@insertSeparators null
                val beforeDate = before?.createdTime?.toLocalDate()
                // 날짜가 달라지는 경계 → 구분선 삽입
                if (beforeDate != afterDate) ChatItem.DateSeparator(afterDate) else null
            }
        }
    }
}
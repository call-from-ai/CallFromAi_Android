package kr.co.call.data.repositoryImpl

import kotlinx.coroutines.delay
import kr.co.call.domain.model.chatting.ChatSummary
import kr.co.call.domain.repository.ChatRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class ChatRepositoryImpl @Inject constructor(

) : ChatRepository {

    //TODO: 추후에 api 연동
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
}
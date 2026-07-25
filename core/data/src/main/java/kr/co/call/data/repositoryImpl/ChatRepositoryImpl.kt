package kr.co.call.data.repositoryImpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kr.co.call.domain.model.chatting.ChatHeader
import kr.co.call.domain.model.chatting.ChatItem
import kr.co.call.domain.model.chatting.ChatSummary
import kr.co.call.domain.model.chatting.ImageData
import kr.co.call.domain.repository.ChatRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

//TODO: 추후에 api 연동
class ChatRepositoryImpl @Inject constructor(

) : ChatRepository {

    private val chatSummaries = mutableListOf(
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

    override suspend fun getChatList(): Result<List<ChatSummary>> = runCatching {
        delay(1000.milliseconds)
        chatSummaries
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
            pagingData.map { msg -> msg as ChatItem }
        }
    }

    private val chatHeaders = mutableMapOf(
        2L to ChatHeader(characterId = 1, characterFirstName = "민지", dDay = 12),
        3L to ChatHeader(characterId = 2, characterFirstName = "서연", dDay = 5),
        4L to ChatHeader(characterId = 3, characterFirstName = "지우", dDay = 30),
        5L to ChatHeader(characterId = 4, characterFirstName = "예린", dDay = 1),
    )

    override suspend fun getChatRoomHeader(roomId: Long): Result<ChatHeader> = runCatching {
        delay(500.milliseconds)
        chatHeaders[roomId] ?: error("ChatHeader not found for roomId=$roomId")
    }

    override suspend fun sendMessage(
        roomId: Long,
        message: String?,
        image: ImageData?
    ): Result<Unit> = runCatching {
        delay(500.milliseconds)

        val imagePart = image?.let {
            MultipartBody.Part.createFormData(
                name = "image",
                filename = it.fileName,
                body = it.bytes.toRequestBody(it.mimeType.toMediaType())
            )
        }
    }

    override suspend fun deleteMessage(messageId: Long): Result<Unit> = runCatching {
        delay(500.milliseconds)

        Unit
    }




}
package kr.co.call.data.repositoryImpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kr.co.call.data.mapper.ChatMapper
import kr.co.call.data.mapper.ChatMapper.toDomain
import kr.co.call.data.util.safeApiResult
import kr.co.call.data.util.safeApiResultUnit
import kr.co.call.domain.model.chatting.ChatHeader
import kr.co.call.domain.model.chatting.ChatItem
import kr.co.call.domain.model.chatting.ChatSummary
import kr.co.call.domain.model.chatting.ImageData
import kr.co.call.domain.repository.ChatRepository
import kr.co.call.network.api.ChatApi
import kr.co.call.network.dto.chatting.MuteChatRoomRequestDTO
import kr.co.call.network.util.ErrorResponseParser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val errorResponseParser: ErrorResponseParser
) : ChatRepository {

    override suspend fun getChatList(): Result<List<ChatSummary>> =
        safeApiResult(errorResponseParser) { chatApi.getChatRoomList() }
            .map { with(ChatMapper) { it.toDomain() } }

    override suspend fun deleteChatRoom(roomId: Long): Result<Unit> =
        safeApiResultUnit(errorResponseParser) { chatApi.deleteChatRoom(roomId) }

    // TODO: 인터페이스에 isMuted 파라미터 추가 필요 (현재 서버 API는 명시적 상태 전달 방식)
    override suspend fun updateAlarmSetting(
        roomId: Long
    ): Result<Unit> =
        safeApiResultUnit(errorResponseParser) {
            chatApi.muteChatRoom(roomId, MuteChatRoomRequestDTO(isMuted = true))
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

    override suspend fun getChatRoomHeader(roomId: Long): Result<ChatHeader> =
        safeApiResult(errorResponseParser) { chatApi.getChatRoomHeader(roomId) }
            .map { with(ChatMapper) { it.toDomain() } }

    override suspend fun sendMessage(
        roomId: Long,
        message: String?,
        image: ImageData?
    ): Result<Unit> {
        val imagePart = image?.let {
            MultipartBody.Part.createFormData(
                name = "image",
                filename = it.fileName,
                body = it.bytes.toRequestBody(it.mimeType.toMediaType())
            )
        }
        return safeApiResultUnit(errorResponseParser) {
            chatApi.sendMessage(roomId, message, imagePart)
        }
    }

    override suspend fun deleteMessage(
        chatroomId: Long,
        messageId: Long
    ): Result<Unit> =
        safeApiResultUnit(errorResponseParser) { chatApi.deleteMessage(chatroomId, messageId) }
}

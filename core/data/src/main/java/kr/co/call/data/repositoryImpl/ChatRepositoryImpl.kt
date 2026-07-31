package kr.co.call.data.repositoryImpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

/**
 * [ChatRepository]의 구현체.
 *
 * 채팅 관련 네트워크 요청을 [ChatApi]를 통해 처리하며,
 * 응답 데이터를 도메인 모델로 변환하여 반환한다.
 *
 * @param chatApi 채팅 관련 Retrofit API 인터페이스
 * @param errorResponseParser API 에러 응답을 파싱하는 유틸리티
 */
class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val errorResponseParser: ErrorResponseParser
) : ChatRepository {

    // 채팅방 목록을 서버에서 조회하여 도메인 모델 리스트로 변환해 반환
    override suspend fun getChatList(): Result<List<ChatSummary>> =
        safeApiResult(errorResponseParser) { chatApi.getChatRoomList() }
            .map { it.toDomain() }

    // 특정 채팅방을 삭제
    override suspend fun deleteChatRoom(roomId: Long): Result<Unit> =
        safeApiResultUnit(errorResponseParser) { chatApi.deleteChatRoom(roomId) }

    // 채팅방의 알람(뮤트) 설정을 변경. isMuted = true 이면 알람 끔, false 이면 알람 켬
    override suspend fun updateAlarmSetting(
        roomId: Long,
        isMuted: Boolean
    ): Result<Unit> =
        safeApiResultUnit(errorResponseParser) {
            chatApi.muteChatRoom(
                roomId,
                MuteChatRoomRequestDTO(isMuted = isMuted)
            )
        }

    // 특정 채팅방의 메시지를 페이징 방식으로 로드
    override fun getChats(roomId: Long): Flow<PagingData<ChatItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ChatPagingSource(chatApi, errorResponseParser, roomId)
            }
        ).flow.map { pagingData ->
            pagingData.map { msg -> msg as ChatItem }
        }
    }

    // 채팅방 헤더 정보(상대방 프로필, 이름 등)를 조회하여 도메인 모델로 변환해 반환
    override suspend fun getChatRoomHeader(roomId: Long): Result<ChatHeader> =
        safeApiResult(errorResponseParser) { chatApi.getChatRoomHeader(roomId) }
            .map { it.toDomain() }

    // 채팅방에 텍스트 또는 이미지 메시지를 전송. 이미지가 있을 경우 multipart 형태로 첨부
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

    // 특정 채팅방의 메시지를 삭제
    override suspend fun deleteMessage(
        chatroomId: Long,
        messageId: Long
    ): Result<Unit> =
        safeApiResultUnit(errorResponseParser) { chatApi.deleteMessage(chatroomId, messageId) }
}

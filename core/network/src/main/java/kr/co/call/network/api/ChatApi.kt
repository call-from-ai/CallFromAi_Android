package kr.co.call.network.api

import kr.co.call.network.dto.ApiResponse
import kr.co.call.network.dto.chatting.ChatHeaderDTO
import kr.co.call.network.dto.chatting.ChatMessageDTO
import kr.co.call.network.dto.chatting.ChatMessagesDTO
import kr.co.call.network.dto.chatting.ChatRoomsDTO
import kr.co.call.network.dto.chatting.MuteChatRoomRequestDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 채팅 관련 API 통신을 위한 Retrofit 인터페이스입니다.
 *
 * 채팅방 목록 조회, 메시지 내역 불러오기, 메시지 전송(텍스트 및 이미지),
 * 읽음 처리 및 알림 설정 등 채팅 서비스에 필요한 주요 기능을 제공합니다.
 */
interface ChatApi {
    @GET("chat-rooms")
    // 채팅방 목록 조회
    suspend fun getChatRoomList(): ApiResponse<ChatRoomsDTO>

    // 채팅방 숨김
    @DELETE("chat-rooms/{chatRoomId}")
    suspend fun deleteChatRoom(
        @Path("chatRoomId") chatRoomId: Long
    ): ApiResponse<Unit>

    // 채팅방의 메시지 목록 조회
    @GET("chat-rooms/{chatRoomId}/messages")
    suspend fun getChats(
        @Path("chatRoomId") chatRoomId: Long,
        @Query("cursor") cursor: Long? = null,
        @Query("size") size: Int? = null
    ): ApiResponse<ChatMessagesDTO>

    // 특정 채팅방의 읽지 않은 상대 메시지를 모두 읽음 처리
    @PATCH("chat-rooms/{chatRoomId}/read")
    suspend fun readChats(
        @Path("chatRoomId") chatRoomId: Long
    ): ApiResponse<Unit>

    // 특정 채팅방의 음소거 상태를 변경
    @PATCH("chat-rooms/{chatRoomId}/mute")
    suspend fun muteChatRoom(
        @Path("chatRoomId") chatRoomId: Long,
        @Body request: MuteChatRoomRequestDTO
    ): ApiResponse<Unit>

    // 채팅방 진입 시 표시할 헤더 정보를 조회
    @GET("chat-rooms/{chatRoomId}")
    suspend fun getChatRoomHeader(
        @Path("chatRoomId") chatRoomId: Long
    ): ApiResponse<ChatHeaderDTO>

    // 텍스트 또는 이미지 메시지 전송 (서버가 항상 multipart/form-data 요구)
    @Multipart
    @POST("chat-rooms/{chatRoomId}/messages")
    suspend fun sendMessage(
        @Path("chatRoomId") chatRoomId: Long,
        @Part("content") content: RequestBody?,
        @Part image: MultipartBody.Part?,
    ): ApiResponse<ChatMessageDTO>

    @DELETE("chat-rooms/{chatRoomId}/messages/{messageId}")
    suspend fun deleteMessage(
        @Path("chatRoomId") chatRoomId: Long,
        @Path("messageId") messageId: Long
    ): ApiResponse<Unit>
}
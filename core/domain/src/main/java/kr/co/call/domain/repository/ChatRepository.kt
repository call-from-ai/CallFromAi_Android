package kr.co.call.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kr.co.call.domain.model.chatting.ChatHeader
import kr.co.call.domain.model.chatting.ChatItem
import kr.co.call.domain.model.chatting.ChatSummary
import kr.co.call.domain.model.chatting.ImageData

interface ChatRepository {

    suspend fun getChatList(): Result<List<ChatSummary>>

    suspend fun deleteChatRoom(roomId: Long): Result<Unit>

    suspend fun updateAlarmSetting(roomId: Long, isMuted: Boolean): Result<Unit>

    fun getChats(roomId: Long): Flow<PagingData<ChatItem>>

    suspend fun getChatRoomHeader(roomId: Long): Result<ChatHeader>

    suspend fun sendMessage(
        roomId: Long,
        message: String?,
        image: ImageData?
    ): Result<Unit>

    suspend fun deleteMessage(
        chatroomId: Long,
        messageId: Long
    ): Result<Unit>
}
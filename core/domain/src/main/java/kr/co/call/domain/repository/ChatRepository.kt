package kr.co.call.domain.repository

import kr.co.call.domain.model.chatting.ChatSummary

interface ChatRepository {

    suspend fun getChatList(): Result<List<ChatSummary>>

    suspend fun deleteChatRoom(roomId: Long): Result<Unit>

    suspend fun updateAlarmSetting(roomId: Long): Result<Unit>
}
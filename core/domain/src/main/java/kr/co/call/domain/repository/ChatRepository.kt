package kr.co.call.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kr.co.call.domain.model.chatting.ChatItem
import kr.co.call.domain.model.chatting.ChatSummary

interface ChatRepository {

    suspend fun getChatList(): Result<List<ChatSummary>>

    suspend fun deleteChatRoom(roomId: Long): Result<Unit>

    suspend fun updateAlarmSetting(roomId: Long): Result<Unit>

    fun getChats(roomId: Long): Flow<PagingData<ChatItem>>
}
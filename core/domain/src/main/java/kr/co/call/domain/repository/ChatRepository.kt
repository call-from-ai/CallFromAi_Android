package kr.co.call.domain.repository

import kr.co.call.domain.model.chatting.ChatSummary

interface ChatRepository {

    suspend fun getChatList(): Result<List<ChatSummary>>
}
package kr.co.call.data.repositoryImpl

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kr.co.call.data.mapper.ChatMapper
import kr.co.call.data.mapper.ChatMapper.toDomain
import kr.co.call.data.util.safeApiResult
import kr.co.call.domain.model.chatting.ChatItem
import kr.co.call.network.api.ChatApi
import kr.co.call.network.util.ErrorResponseParser

/**
 * 특정 채팅방의 메시지를 커서 기반 페이징으로 로드하는 [PagingSource] 구현체.
 *
 * [ChatApi.getChats]를 호출하여 서버에서 메시지를 가져오며,
 * 응답의 [nextCursor]와 [hasNext] 값을 기준으로 다음 페이지 여부를 판단한다.
 * 메시지는 최신순(newest-first)으로 반환되며, reverseLayout = true 와 함께 사용한다.
 *
 * @param chatApi 채팅 관련 Retrofit API 인터페이스
 * @param errorResponseParser API 에러 응답을 파싱하는 유틸리티
 * @param roomId 메시지를 조회할 채팅방 ID
 */
class ChatPagingSource(
    private val chatApi: ChatApi,
    private val errorResponseParser: ErrorResponseParser,
    private val roomId: Long,
) : PagingSource<Long, ChatItem.Message>() {

    /**
     * 페이지 단위로 메시지를 로드한다.
     *
     * [LoadParams.key]가 null이면 최신 메시지부터 조회하고,
     * 값이 있으면 해당 커서 이전 메시지를 조회한다.
     * 다음 페이지가 없으면 [LoadResult.Page.nextKey]를 null로 설정하여 페이징을 종료한다.
     */
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, ChatItem.Message> =
        safeApiResult(errorResponseParser) {
            chatApi.getChats(
                chatRoomId = roomId,
                cursor = params.key,
                size = params.loadSize,
            )
        }.fold(
            onSuccess = { dto ->
                LoadResult.Page(
                    data = dto.toDomain().reversed(),
                    prevKey = null,
                    nextKey = if (dto.hasNext) dto.nextCursor else null,
                )
            },
            onFailure = { LoadResult.Error(it) }
        )

    /**
     * 스크롤 위치 복원 시 사용할 갱신 키를 반환한다.
     * 채팅은 항상 최신 메시지부터 다시 로드하므로 null을 반환한다.
     */
    override fun getRefreshKey(state: PagingState<Long, ChatItem.Message>): Long? = null
}

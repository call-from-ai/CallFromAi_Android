package kr.co.call.domain.model.push

import kr.co.call.domain.model.call.IncomingCall

/**
 * push payload를 [IncomingCall]로 변환합니다.
 */
fun PushPayload.Call.toIncomingCall(): IncomingCall =
    IncomingCall(
        callId = callId,
        characterId = characterId,
        characterName = characterName,
        characterImageUrl = characterImageUrl,
        chatRoomId = chatRoomId,
    )

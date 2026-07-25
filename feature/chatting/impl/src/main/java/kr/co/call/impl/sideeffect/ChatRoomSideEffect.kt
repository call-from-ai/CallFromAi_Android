package kr.co.call.impl.sideeffect

sealed interface ChatRoomSideEffect {
    data class ShowToast(val message: String): ChatRoomSideEffect
    data class Call(val characterId: Long): ChatRoomSideEffect

    data object GoToGallery: ChatRoomSideEffect

    data object GoToCamera: ChatRoomSideEffect
}

package kr.co.call.impl.viewmodel

sealed interface EditProfileSideEffect {
    data object NavigateBack : EditProfileSideEffect
    data class ShowMessage(val message: String) : EditProfileSideEffect
}

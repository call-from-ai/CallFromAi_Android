package kr.co.call.impl.viewmodel

sealed interface EditCharacterSideEffect {
    data object NavigateBack : EditCharacterSideEffect
    data class ShowMessage(val message: String) : EditCharacterSideEffect
    data object ShowEditLimitExceeded : EditCharacterSideEffect
}

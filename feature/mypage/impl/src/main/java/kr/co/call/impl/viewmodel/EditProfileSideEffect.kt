package kr.co.call.impl.viewmodel

sealed interface EditProfileSideEffect {
    data object NavigateBack : EditProfileSideEffect

    /** 저장 성공 후 상위 화면에 즉시 반영할 스냅샷 */
    data class ProfileSaved(
        val nickname: String,
        val profileImageUrl: String,
    ) : EditProfileSideEffect

    data class ShowMessage(val message: String) : EditProfileSideEffect
}

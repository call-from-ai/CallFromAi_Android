package kr.co.call.impl.viewmodel

sealed interface MyPageSideEffect {
    data object NavigateToProfileDetail : MyPageSideEffect
    data object NavigateToChargeTicket : MyPageSideEffect
    data object NavigateToPurchaseTicket : MyPageSideEffect
    data object NavigateToTicketHistory : MyPageSideEffect
    data object NavigateToCharacterManagement : MyPageSideEffect
    data object NavigateToFaq : MyPageSideEffect
    data object NavigateToInquiry : MyPageSideEffect
    data object NavigateToTerms : MyPageSideEffect
    data object ShowLogoutConfirmDialog : MyPageSideEffect
    data object ShowDeleteAccountConfirmDialog : MyPageSideEffect
    data object NavigateToLogin : MyPageSideEffect
    data object NavigateToLanding : MyPageSideEffect
}
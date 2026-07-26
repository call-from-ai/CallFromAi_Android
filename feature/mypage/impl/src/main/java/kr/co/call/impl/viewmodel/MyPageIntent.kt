package kr.co.call.impl.viewmodel

sealed interface MyPageIntent {
    data object ClickProfile : MyPageIntent
    data object ClickChargeTicket : MyPageIntent
    data object ClickPurchaseTicket : MyPageIntent
    data object ClickHistoryTicket : MyPageIntent
    data object ClickCharacterManagement : MyPageIntent
    data object ClickFaq : MyPageIntent
    data object ClickInquiry : MyPageIntent
    data object ClickTerms : MyPageIntent
    data object ClickLogout : MyPageIntent
    data object ClickDeleteAccount : MyPageIntent
    data object ConfirmLogout : MyPageIntent
    data object ConfirmDeleteAccount : MyPageIntent
}
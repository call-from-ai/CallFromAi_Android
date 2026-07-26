package kr.co.call.impl.viewmodel

import kr.co.call.domain.model.mypage.FaqCategory
import kr.co.call.domain.model.mypage.FaqItem
import kr.co.call.domain.util.LoadStatus

data class FaqState(
    val itemsByCategory: Map<FaqCategory, List<FaqItem>> = emptyMap(),
    val loadStatus: LoadStatus = LoadStatus.Idle,
)
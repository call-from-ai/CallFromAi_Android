package kr.co.call.domain.repository

import kr.co.call.domain.model.mypage.FaqCategory
import kr.co.call.domain.model.mypage.FaqItem

interface FaqRepository {
    suspend fun getFaqItems(): Result<Map<FaqCategory, List<FaqItem>>>
}
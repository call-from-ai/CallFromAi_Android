package kr.co.call.impl.viewmodel.state

import java.time.LocalDate

data class Onboarding1State (
    val lastName: String="",
    val firstName: String="",
    val birthday: LocalDate=LocalDate.now(),
    val job: String="",
    val mbti: String="",
    val gender: String="",
    val imageUrl: String=""
)
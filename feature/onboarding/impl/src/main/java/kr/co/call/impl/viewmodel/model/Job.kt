package kr.co.call.impl.viewmodel.model

enum class MemberJob(
    val label: String,
){
    UNIVERSITY_STUDENT("대학생"),
    EMPLOYEE("직장인"),
    OTHER("기타")
}

enum class CharacterJob(
    val label: String,
){
    STUDENT("대학생"),
    EMPLOYED("직장인"),
    UMEMPLOYED("무직")
}

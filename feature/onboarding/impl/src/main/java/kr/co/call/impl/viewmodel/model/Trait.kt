package kr.co.call.impl.viewmodel.model

enum class Trait(
    val keyword: String,
    val emoji: String,
    val label: String,
){
    HUMOROUS("HUMOROUS", "😃", "유머러스한"),
    PLAYFUL("PLAYFUL", "\uD83E\uDD21", "장난기 많은"),
    AFFECTIONATE("AFFECTIONATE", "\uD83D\uDC95", "애교 많은"),
    JEALOUS("JEALOUS", "❤\uFE0F\u200D\uD83D\uDD25", "질투심 폭발"),
    TALKATIVE("TALKATIVE", "\uD83E\uDDCF\uD83C\uDFFB", "수다쟁이"),
    DAD_JOKE_LOVER("DAD_JOKE_LOVER", "\uD83D\uDE1D", "아재개그 좋아하는"),
    HOMEBODY("HOMEBODY", "\uD83D\uDECF\uFE0F", "집순이/집돌이"),
    TEASING("TEASING", "\uD83C\uDFC0", "놀리는 걸 좋아하는"),
    POSSESSIVE("POSSESSIVE", "\uD83D\uDCF1", "집착하는"),
    TSUNDERE("TSUNDERE", "\uD83D\uDC4B\uD83C\uDFFB", "츤데레"),
    EXPRESSIVE("EXPRESSIVE", "\uD83D\uDE0C", "표현을 많이 하는"),
    PET_NAME_LOVER("PET_NAME_LOVER", "\uD83E\uDEE6", "애칭을 자주 쓰는"),
    EXCLUSIVE("EXCLUSIVE", "\uD83E\uDEE6", "독점욕이 있는"),
    QUIRKY("QUIRKY", "\uD83D\uDC4D\uD83C\uDFFB", "4차원 같은"),
    LAID_BACK("LAID_BACK", "\uD83D\uDD76\uFE0F", "털털한"),
    OPENLY_JEALOUS("OPENLY_JEALOUS", "\uD83D\uDCD1", "질투를 숨기지 않는"),
    SHY("SHY", "\uD83E\uDDD0", "부끄러움을 많이 타는"),
    SMOOTH_TALKER("SMOOTH_TALKER", "☄\uFE0F", "능청스러운"),
    FREQUENT_CHECKER("FREQUENT_CHECKER", "\uD83D\uDC8C", "연락을 자주 확인하는"),
    GOOD_LISTENER("GOOD_LISTENER", "\uD83D\uDC50\uD83C\uDFFB", "고민을 잘 들어주는"),
    COMPLIMENTER("COMPLIMENTER", "\uD83D\uDC50\uD83C\uDFFB", "칭찬을 많이 하는");

    companion object {
        fun fromKeyword(key: String): Trait? {
            return entries.find { it.keyword == key } }
        }
    }
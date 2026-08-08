package kr.co.call.network.dto.character

data class MyCharacterDto(
    val characterId: Long,
    val name: String,
    val main: Boolean,
    val createdAt: String,
    val startedAt: String,
    val imageUrl: String? = null,
    val daysTogether: Int,
    /** 대화 이력이 없으면 null (신규 캐릭터 등) */
    val lastMessageAt: String? = null,
)

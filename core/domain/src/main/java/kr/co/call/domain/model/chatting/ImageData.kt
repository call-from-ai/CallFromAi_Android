package kr.co.call.domain.model.chatting

/**
 * 채팅에서 이미지 첨부를 처리하기 위한 이미지 데이터를 나타내는 데이터 클래스입니다.
 *
 * @property bytes 이미지의 원본 바이트 데이터입니다.
 * @property fileName 확장자를 포함한 이미지 파일 이름입니다.
 * @property mimeType 이미지의 미디어 타입입니다. (예: "image/jpeg", "image/png")
 */
data class ImageData(
    val bytes: ByteArray,
    val fileName: String,
    val mimeType: String,
)

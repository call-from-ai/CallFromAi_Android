package kr.co.call.domain.model.chatting

data class ImageData(
    val bytes: ByteArray,
    val fileName: String,
    val mimeType: String,
)

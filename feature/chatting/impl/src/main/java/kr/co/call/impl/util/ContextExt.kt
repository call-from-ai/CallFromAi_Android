package kr.co.call.impl.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kr.co.call.domain.model.chatting.ImageData

/**
 * 현재 객체를 [ImageData] 인스턴스로 변환합니다.
 *
 * 이 확장 함수는 이미지 관련 데이터를 [ImageData] 객체 형태로 변환하여,
 * 애플리케이션 내에서 이미지 처리 또는 UI 표시 시 일관된 데이터 형식으로
 * 사용할 수 있도록 지원합니다.
 *
 * @return 변환된 [ImageData] 객체입니다.
 */
fun Context.toImageData(uri: Uri): ImageData? {
    val resolver = contentResolver

    val bytes = resolver.openInputStream(uri)?.use { inputStream ->
        inputStream.readBytes()
    } ?: return null

    val mimeType = resolver.getType(uri) ?: "application/octet-stream"

    val fileName = resolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME),
        null,
        null,
        null
    )?.use { cursor ->
        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && index != -1) {
            cursor.getString(index)
        } else {
            "image"
        }
    } ?: "image"

    return ImageData(
        bytes = bytes,
        fileName = fileName,
        mimeType = mimeType,
    )
}
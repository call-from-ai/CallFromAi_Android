package kr.co.call.network.util

import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton
import kr.co.call.network.dto.ErrorBodyDto
import retrofit2.HttpException

/**
 * [HttpException] errorBody JSON -> [ErrorBodyDto].
 *
 * errorBody 는 한 번만 읽을 수 있으므로, 이 파서 외에서 다시 string() 하지 말 것
 */
@Singleton
class ErrorResponseParser @Inject constructor(
    private val gson: Gson,
) {

    fun parse(httpException: HttpException): ErrorBodyDto? {
        return runCatching {
            val raw = httpException.response()?.errorBody()?.string()
            if (raw.isNullOrBlank()) return null
            gson.fromJson(raw, ErrorBodyDto::class.java)
        }.getOrNull()
    }
}

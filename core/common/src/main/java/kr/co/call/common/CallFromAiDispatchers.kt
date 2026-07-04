package kr.co.call.common

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val dispatcher: CallFromAiDispatchers)

enum class CallFromAiDispatchers {
    Default,
    IO,
}
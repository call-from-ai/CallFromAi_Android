package kr.co.call.datastore

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

@Singleton
class AuthSessionManager @Inject constructor() {

    private val sessionExpiredEvents = Channel<Unit>(capacity = Channel.CONFLATED)

    val sessionExpired: Flow<Unit> = sessionExpiredEvents.receiveAsFlow()

    fun notifySessionExpired() {
        sessionExpiredEvents.trySend(Unit)
    }
}

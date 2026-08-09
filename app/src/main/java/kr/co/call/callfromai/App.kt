package kr.co.call.callfromai

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import kr.co.call.callfromai.BuildConfig
import kr.co.call.callfromai.incomingcall.notification.CallNotificationManager
import kr.co.call.callfromai.notification.NotificationChannels
import javax.inject.Inject
import timber.log.Timber

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var callNotificationManager: CallNotificationManager

    override fun onCreate() {
        super.onCreate()

        callNotificationManager.createChannel()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        NotificationChannels.ensureAll(this)

        KakaoSdk.init(
            context = this,
            appKey = BuildConfig.KAKAO_NATIVE_APP_KEY,
        )
    }
}

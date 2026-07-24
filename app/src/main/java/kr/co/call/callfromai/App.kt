package kr.co.call.callfromai

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import kr.co.call.callfromai.BuildConfig

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        KakaoSdk.init(
            context=this,
            appKey=BuildConfig.KAKAO_NATIVE_APP_KEY
        )
    }
}
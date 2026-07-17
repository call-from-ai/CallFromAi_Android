package kr.co.call.callfromai

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kr.co.call.callfromai.BuildConfig
import timber.log.Timber

@HiltAndroidApp
class App: Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
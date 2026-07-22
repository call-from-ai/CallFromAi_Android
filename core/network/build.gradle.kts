import java.util.Properties
import kotlin.apply

plugins {
    id("callfromai.android.library")
    id("callfromai.android.hilt")
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

android {
    namespace = "kr.co.call.network"

    defaultConfig {
        //TODO: baseUrl 불러오기
    }

    buildFeatures {
        buildConfig = true
    }

}

dependencies {
    // domain 모듈
    implementation(project(":core:domain"))

    // datastore 모듈 (TokenInterceptor에서 사용)
    implementation(project(":core:datastore"))

    // retrofit, okhttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson.converter)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // gson
    implementation(libs.gson)

    //coroutine
    implementation(libs.kotlinx.coroutines.core)

    // android 의존성
    implementation(libs.androidx.core.ktx)

    // timber
    implementation(libs.timber)

}
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

val baseUrl=localProperties.getProperty("BASE_URL")
    ?: error("BASE_URL가 local.properties에 없습니다.")

android {
    namespace = "kr.co.call.network"

    defaultConfig {
        buildConfigField(
            "String",
            "BASE_URL",
            "\"$baseUrl\"",
        )
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
    api(libs.retrofit)
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
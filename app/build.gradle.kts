import java.util.Properties

plugins {
    id("callfromai.android.application")
    id("callfromai.android.compose")
    id("callfromai.android.hilt")
    id("callfromai.android.orbit")
    alias(libs.plugins.google.services)
}

val localProperties = Properties().apply {
    rootProject.file("local.properties").inputStream().use(::load)
}

val kakaoNativeAppKey =
    localProperties.getProperty("KAKAO_NATIVE_APP_KEY")
        ?:  throw GradleException(
            """
            local.properties에 KAKAO_NATIVE_APP_KEY가 없습니다.
            local.properties에 다음을 추가해주세요.
            KAKAO_NATIVE_APP_KEY= xxxx
            """.trimIndent()
        )

android {
    namespace = "kr.co.call.callfromai"
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        applicationId = "kr.co.call.callfromai"
        versionCode = 1
        versionName = "1.0"

        buildConfigField(
            "String",
            "KAKAO_NATIVE_APP_KEY",
            "\"$kakaoNativeAppKey\"",
        )

        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"]=kakaoNativeAppKey
    }
}

dependencies {
    // feature 모듈
    implementation(project(":feature:login:api"))
    implementation(project(":feature:login:impl"))
    implementation(project(":feature:onboarding:api"))
    implementation(project(":feature:onboarding:impl"))
    implementation(project(":feature:home:api"))
    implementation(project(":feature:home:impl"))
    implementation(project(":feature:chatting:api"))
    implementation(project(":feature:chatting:impl"))
    implementation(project(":feature:call:api"))
    implementation(project(":feature:call:impl"))
    implementation(project(":feature:mypage:api"))
    implementation(project(":feature:mypage:impl"))

    // core 모듈
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:datastore"))

    // 필수 의존성
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.kotlinx.serialization.json)

    // nav3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    // timber
    implementation(libs.timber)

    //kakao login
    implementation(libs.kakao.user)

    // Firebase Cloud Messaging
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    // FirebaseMessaging.token.await()
    implementation(libs.kotlinx.coroutines.play.services)

    // test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

}
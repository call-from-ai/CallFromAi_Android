plugins {
    id("callfromai.android.library")
    id("callfromai.android.compose")
}

android {
    namespace = "kr.co.call.designsystem"
}

dependencies {
    // Compose 의존성은 AndroidComposeConventionPlugin에서 자동 추가됨

    // android 의존성
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.material3)
}
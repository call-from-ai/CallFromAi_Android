plugins {
    id("callfromai.android.library")
    id("callfromai.android.hilt")
}

android {
    namespace = "kr.co.call.core.common"
    compileSdk = 36
}

dependencies {
    // Coroutines는 KotlinLibraryConventionPlugin에서 자동 추가됨
}
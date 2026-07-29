plugins {
    id("callfromai.android.library")
    id("callfromai.android.hilt")
}

android {
    namespace = "kr.co.call.datastore"
}

dependencies {
    // dataStore
    api(libs.androidx.datastore.preferences)

    // hilt
    implementation(libs.hilt.android)

    // android 의존성
    implementation(libs.androidx.core.ktx)

    //timber
    implementation(libs.timber)

    // ApplicationScope을 사용하기 위한 프로젝트 내부 공통 모듈
    implementation(project(":core:common"))
}
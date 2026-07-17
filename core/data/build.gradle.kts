plugins {
    id("callfromai.android.library")
    id("callfromai.android.hilt")
}

android {
    namespace = "kr.co.call.data"
}


dependencies {
    // 다른 core 모듈 의존성
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:datastore"))
    implementation(project(":core:database"))

    // android 의존성
    implementation(libs.androidx.core.ktx)

    // timber
    implementation(libs.timber)
}

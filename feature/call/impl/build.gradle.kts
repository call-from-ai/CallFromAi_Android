plugins {
    id("callfromai.android.library")
    id("callfromai.android.compose")
    id("callfromai.android.hilt")
    id("callfromai.android.orbit")
}

android {
    namespace = "kr.co.call.call.impl"
    compileSdk = 36
}

dependencies {
    implementation(project(":feature:call:api"))

    // core 모듈
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:domain"))

    // nav3
    implementation(libs.androidx.navigation3.runtime)

    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.hilt.navigation.compose)

    // timber
    implementation(libs.timber)

    // coil (통화 화면 진입 시 프로필 이미지 프리로드)
    implementation(libs.coil)

    // test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
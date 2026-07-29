plugins {
    id("callfromai.android.library")
    id("callfromai.android.compose")
    id("callfromai.android.hilt")
    id("callfromai.android.orbit")
}

android {
    namespace = "kr.co.call.login.impl"
    compileSdk = 36
}

dependencies {
    implementation(project(":feature:login:api"))

    // core 모듈
    implementation(project(":core:designsystem"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:datastore")) // 토큰 저장 위해 필요

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

    //kakao login
    implementation(libs.kakao.user)

    // test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
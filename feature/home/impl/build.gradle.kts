plugins {
    id("callfromai.android.library")
    id("callfromai.android.compose")
    id("callfromai.android.hilt")
    id("callfromai.android.orbit")
}

android {
    namespace = "kr.co.call.home.impl"
    compileSdk = 36
}

dependencies {
    implementation(project(":feature:home:api"))

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
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.paging.compose)

    // coil
    implementation(libs.coil.compose)

    // timber
    implementation(libs.timber)

    // test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // media3
    implementation(libs.androidx.media3.exoplayer)
}

plugins {
    id("callfromai.android.library")
    id("callfromai.android.compose")
    id("callfromai.android.hilt")
}
android {
    namespace = "kr.co.call.feature.main"
}

dependencies {
    // core 모듈
    implementation(project(":core:designsystem"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))

    // compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)

    // hilt navigation compose
    implementation(libs.androidx.hilt.navigation.compose)

    // timber
    implementation(libs.timber)
}
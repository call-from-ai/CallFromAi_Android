plugins {
    id("callfromai.android.application")
    id("callfromai.android.compose")
    id("callfromai.android.hilt")
}

android {
    namespace = "kr.co.call.callfromai"
    defaultConfig {
        applicationId = "kr.co.call.callfromai"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    // feature 모듈
    implementation(project(":feature:main:api"))
    implementation(project(":feature:main:impl"))
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

    // timber
    implementation(libs.timber)

    // test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

}
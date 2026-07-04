plugins {
    id("callfromai.android.library")
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
}
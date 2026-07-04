plugins {
    id("callfromai.android.library")
    id("callfromai.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "kr.co.call.database"
}

dependencies {
    implementation(libs.androidx.core.ktx)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}
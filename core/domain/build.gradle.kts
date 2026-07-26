plugins {
    id("callfromai.kotlin.library")
}

dependencies {
    // Coroutines는 KotlinLibraryConventionPlugin에서 자동 추가됨

    // Hilt 생성자 주입(@Inject)에 필요한 JSR-330 어노테이션
    implementation("javax.inject:javax.inject:1")
}

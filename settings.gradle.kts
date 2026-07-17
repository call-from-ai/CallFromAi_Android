pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CallFromAi"
include(":app")
include(":core:domain")
include(":core:data")
include(":core:datastore")
include(":core:database")
include(":core:designsystem")
include(":core:network")
include(":feature:login:api")
include(":feature:onboarding:api")
include(":feature:home:api")
include(":feature:chatting:api")
include(":feature:call:api")
include(":feature:mypage:api")
include(":feature:call:impl")
include(":feature:chatting:impl")
include(":feature:home:impl")
include(":feature:login:impl")
include(":feature:mypage:impl")
include(":feature:onboarding:impl")
include(":core:common")

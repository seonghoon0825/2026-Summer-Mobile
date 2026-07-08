pluginManagement {
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

rootProject.name = "FinalProject"

// 진입점
include(":app")

// core: 하위 계층 (아래로만 의존이 흐른다)
include(":core:model")
include(":core:common")
include(":core:designsystem")
include(":core:data")

// feature: 화면 단위 (서로 직접 의존하지 않는다)
include(":feature:auth")
include(":feature:home")
include(":feature:category")
include(":feature:search")
include(":feature:productdetail")
include(":feature:cart")
include(":feature:order")
include(":feature:mypage")
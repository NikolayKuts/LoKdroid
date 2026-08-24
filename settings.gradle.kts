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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "LoKdroid"
include(":androidApp")
include(":sharedUI")
include(":desktopApp")
include(":webApp")
include(":core")
include(":domain")

project(":androidApp").projectDir = file("demoApp/androidApp")
project(":sharedUI").projectDir = file("demoApp/sharedUI")
project(":desktopApp").projectDir = file("demoApp/desktopApp")
project(":webApp").projectDir = file("demoApp/webApp")
project(":core").projectDir = file("library/core")
project(":domain").projectDir = file("library/domain")

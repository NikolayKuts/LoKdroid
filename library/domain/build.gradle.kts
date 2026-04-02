plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinMultiplatform)
    id("lokdroid-documentation-publishing")
    id("lokdroid-publishing-config")
    id("lokdroid-maven-central-publishing")
    id("lokdroid-maven-local-publishing")
    id("lokdroid-readme-sync")
}

val enableIosTargets = providers
    .gradleProperty("lokdroid.enableIosTargets")
    .orNull
    ?.toBooleanStrictOrNull()
    ?: true

val javaVersion = JavaVersion.toVersion(libs.versions.javaVersion.get())

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = libs.versions.javaVersion.get()
            }
        }
    }
    jvm("desktop") {
        compilations.all {
            kotlinOptions {
                jvmTarget = libs.versions.javaVersion.get()
            }
        }
    }
    if (enableIosTargets) {
        iosX64()
        iosArm64()
        iosSimulatorArm64()
    }

    sourceSets {
        commonMain.dependencies {
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = "com.lib.lokdroid.domain"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.androidLibraryMinSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}
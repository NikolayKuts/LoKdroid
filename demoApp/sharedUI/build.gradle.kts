plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

description = "Shared Compose Multiplatform UI for the LoKdroid demo applications."

val enableIosTargets = providers
    .gradleProperty("lokdroid.enableIosTargets")
    .orNull
    ?.toBooleanStrictOrNull()
    ?: true

val androidJavaVersion = JavaVersion.toVersion(libs.versions.javaVersion.get())
val desktopJvmTarget = "11"

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
                jvmTarget = desktopJvmTarget
            }
        }
    }
    if (enableIosTargets) {
        iosX64().binaries.framework {
            baseName = "sharedUI"
        }
        iosArm64().binaries.framework {
            baseName = "sharedUI"
        }
        iosSimulatorArm64().binaries.framework {
            baseName = "sharedUI"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = "com.lib.lokdroid.demoapp.sharedui"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.androidLibraryMinSdk.get().toInt()
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = androidJavaVersion
        targetCompatibility = androidJavaVersion
    }
}

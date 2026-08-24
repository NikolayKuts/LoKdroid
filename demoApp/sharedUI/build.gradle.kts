import constants.LokdroidBuildConstants

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

val javaVersion = libs.versions.javaVersion.get()
val androidJavaVersion = JavaVersion.toVersion(javaVersion)

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = javaVersion
            }
        }
    }
    jvm("desktop") {
        compilations.all {
            kotlinOptions {
                jvmTarget = javaVersion
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
            implementation(project(LokdroidBuildConstants.modules.core.path))

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

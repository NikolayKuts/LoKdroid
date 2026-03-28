plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)

    id("lokdroid-documentation-publishing")
    id("lokdroid-maven-central-publishing")
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
        val commonMain by getting {
            dependencies {
                api(project(":domain"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

android {
    namespace = "com.library.lokdroid"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.androidLibraryMinSdk.get().toInt()
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}

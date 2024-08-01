import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jetbrains.dokka.DokkaConfiguration

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.lokdroid.custom.plugin")
    id("org.jetbrains.dokka") version libs.versions.dokka.get()
    kotlin("plugin.serialization") version libs.versions.kotlin.get()
}

android {
    namespace = "com.lib.lokdroid"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
}

dependencies {

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.kotlinx.serialization.json)
}

tasks.dokkaHtml.configure {
//    outputDirectory.set(file("docs"))

    dokkaSourceSets {
        configureEach {
            documentedVisibilities.set(
                setOf(
                    DokkaConfiguration.Visibility.PUBLIC,
                    DokkaConfiguration.Visibility.PROTECTED,
                    DokkaConfiguration.Visibility.INTERNAL,
                    DokkaConfiguration.Visibility.PACKAGE,
                    DokkaConfiguration.Visibility.PRIVATE
                )
            )
        }
    }
}

tasks.publishToMavenLocal.dependsOn("dokkaHtml")
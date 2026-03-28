import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

description = "Desktop demo app module for LoKdroid."

kotlin {
    jvmToolchain(11)
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
}

dependencies {
    implementation(project(":core"))
    implementation(project(":sharedUI"))
    implementation(compose.desktop.currentOs)

    testImplementation(kotlin("test"))
}

compose.desktop {
    application {
        mainClass = "com.lib.lokdroid.demoapp.desktop.DesktopMainKt"
    }
}

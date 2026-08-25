// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
    alias(libs.plugins.jetbrainsKotlinMultiplatform) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.vanniktechMavenPublish) apply false
    id("lokdroid-documentation-publishing")
}

// Compose refuses to configure the experimental Kotlin/JS canvas target used by :webApp unless this
// flag is set. It cannot live in gradle.properties, which is not version controlled here because it
// carries publishing credentials, so declaring it in the build script is what keeps CI and fresh
// clones building.
allprojects {
    extra["org.jetbrains.compose.experimental.jscanvas.enabled"] = "true"
}

import constants.LokdroidBuildConstants
import org.jetbrains.kotlin.gradle.targets.js.webpack.WebpackDevtool

plugins {
    alias(libs.plugins.jetbrainsKotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

description = "Web demo app module for LoKdroid."

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                // Emits webApp.js.map next to the bundle. LoKdroid reads it at runtime to report log
                // call sites as Kotlin file and line instead of a position inside the bundled script.
                // The Kotlin default, eval-source-map, produces positions no fetchable map describes.
                // The nosources variant keeps the file and line mappings but leaves the Kotlin sources
                // out of the map, which is all LoKdroid needs and keeps the map from shipping the code.
                devtool = WebpackDevtool.NOSOURCES_SOURCE_MAP
            }
        }
        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(project(LokdroidBuildConstants.modules.core.path))
            implementation(project(LokdroidBuildConstants.modules.sharedUi.path))
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

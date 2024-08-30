plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("lokdroid-documentation-publishing") {
            id = "lokdroid-documentation-publishing"
            implementationClass = "DocumentationPublishingPlugin"
        }

        register("lokdroid-maven-central-publishing") {
            id = "lokdroid-maven-central-publishing"
            implementationClass = "MavenCentralPublishingPlugin"
        }
    }
}

dependencies {
    implementation(libs.build.logic.vanniktech.maven.publish.plugin)
    implementation(libs.build.logic.dokka.plugin)
}
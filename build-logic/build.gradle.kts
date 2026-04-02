plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        setOf(
            "lokdroid-documentation-publishing" to "DocumentationPublishingPlugin",
            "lokdroid-publishing-config" to "PublishingConfigurationPlugin",
            "lokdroid-maven-central-publishing" to "MavenCentralPublishingPlugin",
            "lokdroid-maven-local-publishing" to "MavenLocalPublishingPlugin",
            "lokdroid-readme-sync" to "ReadmeSyncPlugin"
        ).forEach {
            register(it.first) {
                id = it.first
                implementationClass = it.second
            }
        }
    }
}

dependencies {
    implementation(libs.build.logic.vanniktech.maven.publish.plugin)
    implementation(libs.build.logic.dokka.plugin)
}

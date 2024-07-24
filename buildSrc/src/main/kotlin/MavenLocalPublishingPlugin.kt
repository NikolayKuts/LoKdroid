import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType

class MavenLocalPublishingPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.afterEvaluate {
            extensions.configure<PublishingExtension>("publishing") {
                publications {
                    create<MavenPublication>("release") {
                        val libraryVersion = target.rootProject
                            .extensions
                            .getByType<VersionCatalogsExtension>()
                            .named("libs")
                            .findVersion("lokdroid")
                            .get()
                            .displayName

                        // Utilizing components.release requires configuring software components
                        from(components["release"])

                        groupId = "com.lokdroid"
                        artifactId = "core"
                        version = libraryVersion
                    }

                    repositories {
                        mavenLocal()
                    }
                }
            }
        }
    }
}
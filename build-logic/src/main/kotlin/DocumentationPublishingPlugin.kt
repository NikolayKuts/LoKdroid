import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.gradle.DokkaTask

class DocumentationPublishingPlugin : Plugin<Project> {

    override fun apply(project: Project): Unit = with(project) {
        pluginManager.apply("org.jetbrains.dokka")

        project.tasks.named("dokkaHtml", DokkaTask::class.java) {
//            outputDirectory.set(project.file("docs"))

            this.dokkaSourceSets.configureEach {
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

            dependsOn("publishToMavenLocal")
        }
    }
}
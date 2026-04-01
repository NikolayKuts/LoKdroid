import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.register
import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.gradle.DokkaTask

class DocumentationPublishingPlugin : Plugin<Project> {

    companion object {
        private const val DOKKA_PLUGIN_ID = "org.jetbrains.dokka"
        private const val DOKKA_HTML_TASK = "dokkaHtml"
        private const val DOKKA_HTML_MULTI_MODULE_TASK = "dokkaHtmlMultiModule"
        private const val ASSEMBLE_DOCS_SITE_TASK = "assembleDocsSite"
    }

    override fun apply(project: Project): Unit = with(project) {
        pluginManager.apply(DOKKA_PLUGIN_ID)

        tasks.named(DOKKA_HTML_TASK, DokkaTask::class.java) {
            // outputDirectory.set(project.file("docs"))

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
        }

        if (project == rootProject) {
            registerRootDocumentationSiteTask()
        }
    }

    private fun Project.registerRootDocumentationSiteTask() {
        val docsSiteDir = layout.buildDirectory.dir("dokka/html")

        tasks.register<Sync>(ASSEMBLE_DOCS_SITE_TASK) {
            group = "documentation"
            description = "Assembles a GitHub Pages site with multi-module Dokka output."

            dependsOn(DOKKA_HTML_MULTI_MODULE_TASK)
            from(layout.buildDirectory.dir("dokka/htmlMultiModule"))
            into(docsSiteDir)
        }
    }
}

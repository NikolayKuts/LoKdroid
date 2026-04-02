import org.gradle.api.Plugin
import org.gradle.api.Project

class MavenLocalPublishingPlugin : Plugin<Project> {

    companion object {
        private const val LOCAL_PUBLISH_TASK_NAME = "publishAndReleaseToMavenLocal"
        private const val PUBLISHING_GROUP = "publishing"
    }

    override fun apply(project: Project) {
        with(project) {
            if (!pluginManager.hasPlugin(PublishingConfigurationPlugin.LOKDROID_PUBLISHING_CONFIG_PLUGIN_ID)) {
                pluginManager.apply(PublishingConfigurationPlugin.LOKDROID_PUBLISHING_CONFIG_PLUGIN_ID)
            }

            tasks.register(LOCAL_PUBLISH_TASK_NAME) {
                group = PUBLISHING_GROUP
                description = "Publishes LoKdroid library modules to Maven Local."
                dependsOn("publishToMavenLocal")
            }
        }
    }
}

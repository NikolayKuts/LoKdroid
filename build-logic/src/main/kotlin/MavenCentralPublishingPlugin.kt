import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class MavenCentralPublishingPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            if (!pluginManager.hasPlugin(PublishingConfigurationPlugin.LOKDROID_PUBLISHING_CONFIG_PLUGIN_ID)) {
                pluginManager.apply(PublishingConfigurationPlugin.LOKDROID_PUBLISHING_CONFIG_PLUGIN_ID)
            }

            extensions.configure<MavenPublishBaseExtension> {
                publishToMavenCentral()
                signAllPublications()
            }
        }
    }
}

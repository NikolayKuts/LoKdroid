import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class PublishingConfigurationPlugin : Plugin<Project> {

    companion object {
        const val LOKDROID_PUBLISHING_CONFIG_PLUGIN_ID = "lokdroid-publishing-config"
        
        private const val VANNIKTECH_MAVEN_PUBLISH_PLUGIN_ID = "com.vanniktech.maven.publish"
        private const val LOKDROID_LIBRARY_ALIAS = "lokdroid"
        private const val CORE_PROJECT_NAME = "core"

        private const val LOKDROID_LIBRARY_NAME = "LoKdroid"
        private const val DESCRIPTION = "A library for logging in Android applications"
        private const val INCEPTION_YEAR = "2024"
        private const val LICENSE_NAME = "The Apache Software License, Version 2.0"
        private const val LICENSE_URL = "http://www.apache.org/licenses/LICENSE-2.0.txt"
        private const val DEVELOPER_ID = "NikolayKuts"
        private const val DEVELOPER_NAME = "Nikolay Kuts"
        private const val DEVELOPER_EMAIL = "kuts.nikolay.l@gmail.com"
        private const val SOURCE_CONTROL_MANAGEMENT_URL = "https://github.com/NikolayKuts/LoKdroid"
    }

    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply(VANNIKTECH_MAVEN_PUBLISH_PLUGIN_ID)

            extensions.configure<MavenPublishBaseExtension> {
                val library = getLibsLibrary(alias = LOKDROID_LIBRARY_ALIAS)
                val artifactId = if (project.name == CORE_PROJECT_NAME) {
                    library.artifactId
                } else {
                    "${library.artifactId}-${project.name}"
                }

                coordinates(
                    groupId = library.groupId,
                    artifactId = artifactId,
                    version = library.version
                )

                pom {
                    name.set(LOKDROID_LIBRARY_NAME)
                    description.set(DESCRIPTION)
                    inceptionYear.set(INCEPTION_YEAR)
                    url.set(SOURCE_CONTROL_MANAGEMENT_URL)

                    licenses {
                        license {
                            name.set(LICENSE_NAME)
                            url.set(LICENSE_URL)
                        }
                    }

                    developers {
                        developer {
                            id.set(DEVELOPER_ID)
                            name.set(DEVELOPER_NAME)
                            email.set(DEVELOPER_EMAIL)
                        }
                    }

                    scm {
                        url.set(SOURCE_CONTROL_MANAGEMENT_URL)
                    }
                }

                configureBasedOnAppliedPlugins()
            }
        }
    }
}

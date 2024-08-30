import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class MavenCentralPublishingPlugin : Plugin<Project> {

    companion object {

        private const val VANNIKTECH_MAVEN_PUBLISH_PLUGIN_ID = "com.vanniktech.maven.publish"
        private const val LOKDROID_LIBRARY_ALIAS = "lokdroid"

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

    /**
     * command to run library publishing on Maven Central:
     * ./gradlew publishAndReleaseToMavenCentral --no-configuration-cache
     */

    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply(VANNIKTECH_MAVEN_PUBLISH_PLUGIN_ID)

            extensions.configure<MavenPublishBaseExtension> {
                val library = getLibsLibrary(alias = LOKDROID_LIBRARY_ALIAS)

                library.run {
                    println("retrieved libsLibrary { groupId: $groupId, artifactId: $artifactId, version: $version")
                }

                coordinates(
                    groupId = library.groupId,
                    artifactId = library.artifactId,
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

                    // Specify SCM information
                    scm { url.set(SOURCE_CONTROL_MANAGEMENT_URL) }
                }

                // Configure publishing to Maven Central
                publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

                // Enable GPG signing for all publications
                signAllPublications()
            }
        }
    }
}
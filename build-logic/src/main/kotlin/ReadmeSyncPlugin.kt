import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ReadmeSyncPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val syncReadme = project.tasks.register("syncReadmeVersion") {
            group = "versionSync"
            description = "Syncs README dependency version with gradle/libs.versions.toml (versions.lokdroid)."

            doLast {
                val tomlFile = File(project.rootDir, "gradle/libs.versions.toml")
                val readmeFile = File(project.rootDir, "README.md")

                if (!tomlFile.exists() || !readmeFile.exists()) {
                    println("[syncReadmeVersion] Required files not found. Skipping.")
                    return@doLast
                }

                val tomlText = tomlFile.readText()
                val versionRegex = Regex("(?m)^\\s*lokdroid\\s*=\\s*\"([^\"]+)\"\\s*$")
                val version = versionRegex.find(tomlText)?.groupValues?.get(1)
                    ?: error("[syncReadmeVersion] Could not find 'lokdroid' version in libs.versions.toml")

                var readme = readmeFile.readText()
                val patterns = listOf(
                    Regex("implementation\\(\\\"io\\.github\\.nikolaykuts:lokdroid:([^)\\\"]+)\\\"\\)"),
                    Regex("implementation \\\'io\\.github\\.nikolaykuts:lokdroid:([^\\']+)\\\'")
                )

                var changed = false
                patterns.forEach { pattern ->
                    val newReadme = readme.replace(pattern) { match ->
                        val full = match.value
                        val current = match.groups[1]?.value ?: return@replace full
                        if (current == version) return@replace full
                        changed = true
                        if (full.startsWith("implementation(\"")) {
                            "implementation(\"io.github.nikolaykuts:lokdroid:$version\")"
                        } else {
                            "implementation 'io.github.nikolaykuts:lokdroid:$version'"
                        }
                    }
                    readme = newReadme
                }

                if (changed) {
                    readmeFile.writeText(readme)
                    println("[syncReadmeVersion] README.md updated to version $version")
                } else {
                    println("[syncReadmeVersion] README.md already up to date ($version)")
                }
            }
        }

        project.plugins.withId("org.jetbrains.dokka") {
            project.tasks.matching { it.name == "dokkaHtml" }.configureEach {
                dependsOn(syncReadme)
            }
        }
    }
}

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

private const val VERSION_CATALOG_NAME = "libs"

fun Project.getLibsVersion(alias: String): String {
    return rootProject
        .extensions
        .getByType<VersionCatalogsExtension>()
        .named(VERSION_CATALOG_NAME)
        .findVersion(alias)
        .get()
        .displayName
}

fun Project.getLibsLibrary(alias: String): LibsLibrary {
    return rootProject
        .extensions
        .getByType<VersionCatalogsExtension>()
        .named(VERSION_CATALOG_NAME)
        .findLibrary(alias)
        .get()
        .get()
        .toLibsLibrary()
}

fun MinimalExternalModuleDependency.toLibsLibrary(): LibsLibrary = LibsLibrary(
    groupId = group ?: throw IllegalArgumentException("LibsLibrary groupId is null"),
    artifactId = module.name ?: throw IllegalArgumentException("LibsLibrary artifactId is null"),
    version = version ?: throw IllegalArgumentException("LibsLibrary version is null")
)
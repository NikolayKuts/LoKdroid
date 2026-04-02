package constants

data class LokdroidModule(
    val name: String,
    val path: String,
    val directory: String
)

object LokdroidBuildConstants {

    val modules = Modules
}

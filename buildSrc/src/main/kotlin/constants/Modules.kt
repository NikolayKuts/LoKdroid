package constants

object Modules {

    val androidApp = LokdroidModule(
        name = "androidApp",
        path = ":androidApp",
        directory = "demoApp/androidApp"
    )
    val sharedUi = LokdroidModule(
        name = "sharedUI",
        path = ":sharedUI",
        directory = "demoApp/sharedUI"
    )
    val desktopApp = LokdroidModule(
        name = "desktopApp",
        path = ":desktopApp",
        directory = "demoApp/desktopApp"
    )
    val core = LokdroidModule(
        name = "core",
        path = ":core",
        directory = "library/core"
    )
    val domain = LokdroidModule(
        name = "domain",
        path = ":domain",
        directory = "library/domain"
    )

    val all = listOf(androidApp, sharedUi, desktopApp, core, domain)
    val library = listOf(core, domain)
}
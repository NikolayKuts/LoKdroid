# Changelog

## Unreleased

### Changed
- Flattened Gradle project paths to `:core`, `:domain`, `:androidApp`, `:desktopApp`, and `:sharedUI` while keeping `library/` and `demoApp/` as plain directories.
- Migrated the core module to Kotlin Multiplatform with Android, desktop JVM, and iOS targets.
- Moved shared logging logic into `commonMain` and kept Android-specific implementations in `androidMain`.
- Renamed `ILogBuilder` / `LogBuilder` to `IMessageBuilder` / `MessageBuilder`.
- Replaced the public and internal message builder provider abstraction with a simple `messageBuilderFactory` lambda.
- Updated the project to Kotlin `2.1.20` and adopted the Compose compiler plugin in the demo modules.
- Replaced the old Android-only sample app with `demoApp/androidApp`, `demoApp/desktopApp`, and a shared Compose Multiplatform UI module in `demoApp/sharedUI`.
- Converted the desktop demo host from a Kotlin Multiplatform module to a plain JVM Compose Desktop module.
- Removed the Gradle-based `iosApp` module and reserved `demoApp/iosApp` for a future native Xcode host app.

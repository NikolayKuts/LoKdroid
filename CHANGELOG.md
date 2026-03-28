# Changelog

## Unreleased

### Changed
- Restructured the library into nested `:library:core` and `:library:domain` modules.
- Migrated the core module to Kotlin Multiplatform with Android, desktop JVM, and iOS targets.
- Moved shared logging logic into `commonMain` and kept Android-specific implementations in `androidMain`.
- Renamed `ILogBuilder` / `LogBuilder` to `IMessageBuilder` / `MessageBuilder`.
- Replaced the public and internal message builder provider abstraction with a simple `messageBuilderFactory` lambda.
- Updated the project to Kotlin `2.1.20` and switched the sample app to the Compose compiler plugin.

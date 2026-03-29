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
- Removed the Gradle-based `iosApp` module and added a native Xcode host app under `demoApp/iosApp` backed by the shared Compose Multiplatform UI framework.
- Added a desktop `FormatterBuilder` with IDE-friendly caller references and wired it into the Android and desktop demo apps.
- Aligned the default desktop tag with Android-style caller class resolution.
- Updated the default desktop and iOS console output to `Tag<TAB>[Level] emoji message`.
- Added desktop tests covering the formatter builder and default tag provider.
- Raised the Android library minimum SDK to `25`.
- Bumped the published library version to `0.1.0-alpha` and updated README dependency examples.
- Moved `FormatterBuilder` into `commonMain` and reduced platform-specific formatting differences to a small caller-reference hook.
- Introduced a shared `CallSite` model plus common `TagProvider` and line-reference helpers to normalize caller resolution across Android, desktop, and iOS.
- Extracted shared level-to-emoji formatting and switched the default desktop and iOS console markers from circular dots to square color blocks.
- Expanded KDoc coverage across the shared and platform-specific logging infrastructure.
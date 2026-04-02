# Changelog

## Unreleased

## 0.1.2-alpha

### Changed
- Unified Android, desktop JVM, and iOS caller resolution on top of a shared call-site helper that skips internal LoKdroid wrapper frames before selecting the user-facing caller.
- Switched the default tag provider to file-based tags without the `.kt` suffix instead of class-display tags.
- Improved Kotlin/Native symbol parsing so iOS builder logs, synthetic default wrappers, and ObjC-shaped signatures resolve cleaner caller metadata.
- Updated Android caller resolution to use the same shared wrapper-skipping strategy as desktop and iOS instead of a fixed stack offset.
- Added smoke-style Android, desktop, and iOS logging tests for manual log inspection.
- Added broader iOS caller-resolution fixtures to exercise nested classes, enums, singletons, companions, constructors, accessors, wrappers, and ObjC-shaped entry points.
- Updated README and KDoc to describe file-based tags and the current caller-resolution behavior.

## 0.1.0-alpha

### Changed
- Migrated the library to Kotlin Multiplatform with shared `:core` and `:domain` modules targeting Android, desktop JVM, and iOS.
- Flattened Gradle project paths to `:core`, `:domain`, `:androidApp`, `:desktopApp`, and `:sharedUI` while keeping `library/` and `demoApp/` as plain directories on disk.
- Moved shared logging logic into `commonMain` and kept Android-specific implementations in `androidMain`.
- Renamed `ILogBuilder` / `LogBuilder` to `IMessageBuilder` / `MessageBuilder`.
- Replaced the public and internal builder-provider abstraction with a simple `messageBuilderFactory` lambda.
- Shared `FormatterBuilder` across Android, desktop JVM, and iOS and reduced platform-specific formatting differences to a caller-reference hook.
- Introduced a shared `CallSite` model plus common tag and line-reference helpers to normalize multiplatform caller metadata.
- Added desktop formatter support with IDE-friendly caller references and aligned desktop/iOS console output to `Tag<TAB>[Level] emoji message`.
- Extracted shared level-to-emoji formatting and switched desktop/iOS level markers to square color blocks.
- Reorganized the demo applications into `demoApp/androidApp`, `demoApp/desktopApp`, `demoApp/sharedUI`, and a native Xcode host in `demoApp/iosApp`.
- Updated the project to Kotlin `2.1.20`, adopted the Compose compiler plugin in demo modules, and raised the Android library minimum SDK to `25`.
- Expanded KDoc coverage and refreshed the README to match the multiplatform API and demo layout.

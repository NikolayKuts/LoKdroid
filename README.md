# LoKdroid

[![Maven Central](https://img.shields.io/maven-central/v/io.github.nikolaykuts/lokdroid)](https://central.sonatype.com/artifact/io.github.nikolaykuts/lokdroid)

LoKdroid is a Kotlin Multiplatform logging library with a shared core API for Android, desktop JVM, and iOS, plus Android-specific extensions for file and Logcat-oriented logging.

## Features

- **Shared logging API**: Use the same `LoKdroid`, log functions, and builder DSL from common code.
- **Multiplatform targets**: Core logging works on Android, desktop JVM, and iOS.
- **Shared formatter DSL**: `FormatterBuilder` is available from shared code and formats logs on all three platforms.
- **Customizable formatting and tagging**: Override formatter, logger, tag provider, and message builder factory.
- **Platform-native console behavior**: Android uses `Logcat`; desktop JVM and iOS use formatted console output with emoji level markers.
- **Android-specific extensions**: File logging, Logcat-formatted files, and remote logging remain available on Android.
- **Compose demo apps**: The repository includes Android, desktop, and native iOS demo hosts backed by a shared Compose Multiplatform UI module.

## Documentation
View **[KDoc](https://nikolaykuts.github.io/LoKdroid/)**

## Repository Layout

The repository uses flat Gradle project paths while keeping grouped folders on disk:

```text
LoKdroid/
├── library/
│   ├── core/       -> Gradle module :core
│   └── domain/     -> Gradle module :domain
└── demoApp/
    ├── androidApp/ -> Gradle module :androidApp
    ├── desktopApp/ -> Gradle module :desktopApp
    ├── sharedUI/   -> Gradle module :sharedUI
    └── iosApp/     -> native iOS Xcode host app, not part of the Gradle build
```

Notes:
- `library/` and `demoApp/` are directories for organization, not Gradle modules.
- `:sharedUI` contains the shared Compose Multiplatform screens used by the Android, desktop, and iOS demo hosts.
- `demoApp/iosApp` is a native Xcode application that imports the `sharedUI` framework and hosts Compose UI through `MainViewController`.

## Getting Started

Add LoKdroid to your project:

```gradle
repositories {
    mavenCentral()
}
```

**Kotlin DSL**
```gradle
dependencies {
    implementation("io.github.nikolaykuts:lokdroid:0.1.1-alpha")
}
```

**Groovy**
```gradle
dependencies {
    implementation 'io.github.nikolaykuts:lokdroid:0.1.1-alpha'
}
```

For a Kotlin Multiplatform project, put the dependency in `commonMain` when you need the shared API there. `FormatterBuilder` is part of the shared API and can be used from code that targets Android, desktop JVM, and iOS. Android-specific implementations such as `FileLogger`, `ConsoleAndFileLogger`, `RemoteLogger`, and `FileFormat` are available only from `androidMain`.

Initialize LoKdroid before logging starts:

```kotlin
LoKdroid.initialize()
```

## Running Demo Apps

- Android: run the `androidApp` application module from Android Studio.
- Desktop: run `./gradlew :desktopApp:run`
- iOS: open `demoApp/iosApp/iosApp.xcodeproj` in Xcode and run the `iosApp` scheme.

### iOS Demo Host Setup

The repository already contains a native iOS host app under `demoApp/iosApp`.

- The shared Kotlin UI is exposed from `:sharedUI` as the `sharedUI` framework.
- The Swift app initializes logging through `MainViewControllerKt.initializeLoKdroid()`.
- The root Compose screen is embedded through `MainViewControllerKt.MainViewController()`.

Minimal integration looks like this on the Swift side:

```swift
import SwiftUI
import sharedUI

@main
struct iosAppApp: App {
    init() {
        MainViewControllerKt.initializeLoKdroid()
    }

    var body: some Scene {
        WindowGroup {
            ComposeRootView()
        }
    }
}
```

## Usage

### Single logging

```kotlin
logV(message = "some message")
logD(message = "some message")
logI(message = "some message")
logW(message = "some message")
logE(message = "some message")
```
#### Output
<img width="1551" alt="image" src="https://github.com/user-attachments/assets/92a2e541-ef81-4e22-a6fc-82cbfe53d541">


### Multiple logging via IMessageBuilder

```kotlin

val year: Int = currentDate.year
val month: Int = currentDate.monthValue
val dayOfWeek: java.time.DayOfWeek = currentDate.dayOfWeek

logI {
    "Date:"()
    "Year: $year"()
    "Month: $month"()
    "Day of the Week: $dayOfWeek"()
}
```
#### Output

<img width="1545" alt="image" src="https://github.com/user-attachments/assets/1469f691-b73b-437e-b7e0-f6a2d2dcda1a">

### Multiple logging with per-line levels

Inside the `IMessageBuilder` DSL, the string extension function accepts an optional `Level`.
The default `MessageBuilder` uses that value to prepend an emoji marker for the corresponding line.

Shortcuts available inside the builder:
- `V` for `Level.Verbose`
- `D` for `Level.Debug`
- `I` for `Level.Info`
- `W` for `Level.Warn`
- `E` for `Level.Error`

```kotlin
logV {
    "multi log"()
    "some Error"(E)
    "some Info"(I)
    "some Debug"(D)
    "some Verbose"(V)
    "some Warn"(W)
}
```

When used with the `FormatterBuilder` configuration from the next section, the output can look like this:

```text
DesktopMainKt    [Verbose] ⬜ --->    DesktopMainKt.invoke(DesktopMain.kt:24) multi log
    🟥 -> some Error
    🟩 -> some Info
    🟦 -> some Debug
    ⬜ -> some Verbose
    🟨 -> some Warn some custom text
```

### Formatter Builder

`FormatterBuilder` is shared and can be configured the same way for Android, desktop JVM, and iOS by passing the built `IFormatter` into `LoKdroid.initialize`.

Platform behavior for `withLineReference()`:
- Android: inserts a compact reference like `MainScreen.kt:42`
- Desktop JVM: inserts a stack-frame-style reference like `MainScreen.onClick(MainScreen.kt:42)`
- iOS: inserts the same stack-frame-style reference format as desktop

```kotlin
LoKdroid.initialize(
    formatter = FormatterBuilder()
        .withPointer()        // adds "--->"
        .space()              // adds a space
        .withLineReference()  // Android: File.kt:123, Desktop/iOS: Class.method(File.kt:123)
        .space()
        .message()            // MANDATORY: injects your original log message
        .space()
        .custom(text = "some custom text") // appends any custom text
        .build()
)
```

### Default Console Behavior

- Android `ConsoleLogger` writes through the platform logging system, so logs appear in Logcat with Android priorities.
- Desktop JVM and iOS `ConsoleLogger` write formatted lines to standard output using the pattern `Tag<TAB>[Level] emoji message`.
- The default desktop and iOS level markers use square emoji blocks: `⬜`, `🟦`, `🟩`, `🟨`, `🟥`.

Desktop and iOS example output:

```text
SharedUiScreensKt    [Debug] 🟦 --->    SharedUiScreensKt.invokeMultipleLog(SharedUiScreens.kt:293) Multiple log
```

## Default Initialization

```kotlin
fun initialize(
    minLevel: Level = Level.Verbose,
    logger: ILogger = ConsoleLogger,
    formatter: IFormatter = IFormatter { message -> message },
    tagProvider: () -> String = { TagProvider.getTag() },
    messageBuilderFactory: () -> IMessageBuilder = { MessageBuilder() }
)
```

Available implementations:
- `ConsoleLogger` is available on Android, desktop JVM, and iOS.
- `FileLogger` is Android-only and writes messages to a file. It supports Android Studio Logcat import format.
- `ConsoleAndFileLogger` is Android-only.
- `RemoteLogger` is currently Android-only.

## Customizing
```kotlin
/** custom implementation */

LoKdroid.initialize(
    minLevel = Level.Debug,
    logger = { level: Level, tag: String, message: String -> /** your logic */ },
    formatter = { message -> "return formatted message: $message" },
    tagProvider = { "custom tag" },
    messageBuilderFactory = {
        object : IMessageBuilder {
            override fun build(): String = "build your string"

            override operator fun String.invoke(level: Level?) {
                // use this block to build multiple message lines
            }
        }
    }
)
```

Platform visibility:
- `commonMain` sees the shared API, including `LoKdroid`, log functions, `ILogger`, `IFormatter`, `IMessageBuilder`, `ConsoleLogger`, and `FormatterBuilder`.
- `androidMain` also sees Android-only implementations such as `FileLogger`, `ConsoleAndFileLogger`, `RemoteLogger`, and `FileFormat`.
- `desktopMain` and `iosMain` use the same shared API surface and platform-specific console behavior under the hood.

Console defaults:
- The default desktop tag is derived from the caller class name, matching Android-style tag resolution.
- iOS uses the same console output shape as desktop.
- Top-level Kotlin functions on desktop and iOS are tagged with their synthetic file owner, for example `SharedUiScreensKt`.

## License

This project is licensed under the Apache License, Version 2.0. See the [LICENSE](https://github.com/NikolayKuts/LoKdroid/blob/maven_central/LICENSE.md) file for more details.

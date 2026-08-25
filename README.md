# LoKdroid

[![Maven Central](https://img.shields.io/maven-central/v/io.github.nikolaykuts/lokdroid)](https://central.sonatype.com/artifact/io.github.nikolaykuts/lokdroid)

LoKdroid is a Kotlin Multiplatform logging library with a shared core API for Android, desktop JVM, iOS, and Kotlin/JS in the browser, plus Android-specific extensions for file and Logcat-oriented logging.

## Features

- **Shared logging API**: Use the same `LoKdroid`, log functions, and builder DSL from common code.
- **Multiplatform targets**: Core logging works on Android, desktop JVM, iOS, and Kotlin/JS in the browser.
- **Shared formatter DSL**: `FormatterBuilder` is available from shared code and formats logs on every target.
- **Customizable formatting and tagging**: Override formatter, logger, tag provider, and multi-line message builder factory.
- **Best-effort caller resolution**: Shared caller normalization skips internal LoKdroid wrapper frames on every target. In the browser, bundled stack positions are mapped back through source maps to the original `.kt` file and line.
- **Platform-native console behavior**: Android uses `Logcat`; desktop JVM and iOS use formatted console output with emoji level markers; the browser routes each level to the matching `console` method.
- **Android-specific extensions**: File logging, Logcat-formatted files, and remote logging remain available on Android.
- **Compose demo apps**: The repository includes Android, desktop, native iOS, and browser demo hosts backed by a shared Compose Multiplatform UI module.

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
    ├── webApp/     -> Gradle module :webApp
    ├── sharedUI/   -> Gradle module :sharedUI
    └── iosApp/     -> native iOS Xcode host app, not part of the Gradle build
```

Notes:
- `library/` and `demoApp/` are directories for organization, not Gradle modules.
- `:sharedUI` contains the shared Compose Multiplatform screens used by the Android, desktop, iOS, and browser demo hosts.
- `demoApp/iosApp` is a native Xcode application that imports the `sharedUI` framework and hosts Compose UI through `MainViewController`.

## Getting Started

### Requirements

The published `:core` and `:domain` artifacts are compiled for **Java 17**, so consuming projects need
a JDK 17 toolchain and a matching `jvmTarget` / `compileOptions` setting.

Add LoKdroid to your project:

```gradle
repositories {
    mavenCentral()
}
```

**Kotlin DSL**
```gradle
dependencies {
    implementation("io.github.nikolaykuts:lokdroid:0.2.0-alpha")
}
```

**Groovy**
```gradle
dependencies {
    implementation 'io.github.nikolaykuts:lokdroid:0.2.0-alpha'
}
```

For a Kotlin Multiplatform project, put the dependency in `commonMain` when you need the shared API there. `FormatterBuilder` is part of the shared API and can be used from code that targets Android, desktop JVM, and iOS. Android-specific implementations such as `FileLogger`, `ConsoleAndFileLogger`, `RemoteLogger`, and `FileFormat` are available only from `androidMain`.

LoKdroid works out of the box and does not require explicit initialization before logging starts. If you do nothing, it uses the default console logger, identity formatter, caller-based tag provider, and default message builder:

```kotlin
logI(message = "LoKdroid is ready")
```

Call `LoKdroid.initialize(...)` only when you want to override those defaults.

## Running Demo Apps

- Android: run the `androidApp` application module from Android Studio.
- Desktop: run `./gradlew :desktopApp:run`
- iOS: open `demoApp/iosApp/iosApp.xcodeproj` in Xcode and run the `iosApp` scheme.
- Web: run `./gradlew :webApp:jsBrowserDevelopmentRun` and open the printed `localhost` address.

### iOS Demo Host Setup

The repository already contains a native iOS host app under `demoApp/iosApp`.

- The shared Kotlin UI is exposed from `:sharedUI` as the `sharedUI` framework.
- The Swift app customizes the default logging configuration through `MainViewControllerKt.initializeLoKdroid()`.
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
The default `MultipleLineMessageBuilder` uses that value to prepend an emoji marker for the corresponding line.

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
DesktopMain    [Verbose] ⬜ --->    DesktopMainKt.invoke(DesktopMain.kt:24) multi log
    🟥 -> some Error
    🟩 -> some Info
    🟦 -> some Debug
    ⬜ -> some Verbose
    🟨 -> some Warn some custom text
```

### Formatter Builder

`FormatterBuilder` is shared and can be configured the same way on every target by passing the built `IFormatter` into `LoKdroid.initialize`.

Platform behavior for `withLineReference()`:
- Android: inserts a compact reference like `MainScreen.kt:42`
- Desktop JVM: inserts a stack-frame-style reference like `MainScreen.onClick(MainScreen.kt:42)`
- iOS: inserts the same stack-frame-style reference format as desktop
- Web: inserts the same stack-frame-style reference resolved back to Kotlin sources, such as `SharedUiScreensKt.onClick(SharedUiScreens.kt:77)`

Before formatting the line reference, LoKdroid skips its own internal wrapper frames such as `LoKdroid`, `LogManager`, and `LogFunctions*`, then uses the first external frame that remains.

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

### Browser Line References

In the browser a stack frame points into the bundled script, for example `webApp.js:26276:7`. LoKdroid
translates that position back to Kotlin by reading the source map the script declares for itself, so the
reference names the original file and line the log was written on:

```text
SharedUiScreens    [Debug] 🟦 --->    SharedUiScreensKt.onClick(SharedUiScreens.kt:78) first
```

This requires the bundle's source map to be reachable over HTTP next to the script, which means the
webpack devtool has to emit a real `.map` file rather than inline modules through `eval`:

```kotlin
import org.jetbrains.kotlin.gradle.targets.js.webpack.WebpackDevtool

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                devtool = WebpackDevtool.NOSOURCES_SOURCE_MAP
            }
        }
        binaries.executable()
    }
}
```

`nosources-source-map` is the recommended setting: LoKdroid only reads the file and line mappings, so
leaving the Kotlin sources out of the map keeps it smaller and keeps your code out of the shipped map.
Use `WebpackDevtool.SOURCE_MAP` instead when you also want browser developer tools to display and step
through the original `.kt` files, which needs the sources embedded.

Notes:
- The Kotlin/JS default devtool is `eval-source-map`, whose positions no fetchable source map
  describes. LoKdroid never guesses a Kotlin line in that case. Because that devtool keeps every Kotlin
  module as its own script, the caller is still identified and reported against the generated file, as
  in `SharedUiDemoApp(LoKdroid-sharedUI.js:226)`, with the tag falling back to the module name.
- If the bundle is a single script and its map cannot be reached at all, LoKdroid has no way to tell its
  own frames from yours, so it reports the `????` tag and `at ???(Unknown Source)` instead of a guess.
- The map is downloaded and indexed once, on the first log call, and reused for every later call. A
  script whose map cannot be reached is remembered as such, so a missing map is never re-requested.
- Minified production bundles still resolve the correct file and line; only the method name is lost to
  minification.

### Default Console Behavior

- Android `ConsoleLogger` writes through the platform logging system, so logs appear in Logcat with Android priorities.
- Desktop JVM and iOS `ConsoleLogger` write formatted lines to standard output using the pattern `Tag<TAB>[Level] emoji message`.
- Web `ConsoleLogger` writes the same pattern to the browser console, routing `Verbose` and `Debug` to `console.log`, `Info` to `console.info`, `Warn` to `console.warn`, and `Error` to `console.error`.
- The default desktop and iOS level markers use square emoji blocks: `⬜`, `🟦`, `🟩`, `🟨`, `🟥`.
- The default `tagProvider` derives the tag from the caller file name without the `.kt` extension when source metadata is available.

Desktop and iOS example output:

```text
SharedUiScreens    [Debug] 🟦 --->    SharedUiScreensKt.invokeMultipleLog(SharedUiScreens.kt:293) Multiple log
```

## Default Configuration

LoKdroid starts with this configuration automatically, even if `initialize(...)` is never called:

```kotlin
fun initialize(
    minLevel: Level = Level.Verbose,
    logger: ILogger = ConsoleLogger,
    formatter: IFormatter = IFormatter { message -> message },
    tagProvider: () -> String = { TagProvider.getTag() },
    multipleLineMessageBuilderFactory: () -> IMessageBuilder = { MultipleLineMessageBuilder() }
)
```

Calling `LoKdroid.initialize(...)` later simply replaces the current configuration with your custom one.

Available implementations:
- `ConsoleLogger` is available on Android, desktop JVM, iOS, and the browser.
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
    multipleLineMessageBuilderFactory = {
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
- `desktopMain`, `iosMain`, and `jsMain` use the same shared API surface and platform-specific console behavior under the hood.

Console defaults:
- The default tag on every target is derived from the caller file name, for example `SharedUiScreens`.
- Desktop, iOS, and the browser keep stack-frame-style line references, so top-level Kotlin functions still appear with synthetic owners such as `SharedUiScreensKt.invoke(...)`.
- Caller resolution skips internal LoKdroid wrappers only. If you log through your own helper or extension function, that helper becomes the resolved caller unless you provide a custom `tagProvider`.

## License

This project is licensed under the Apache License, Version 2.0. See the [LICENSE](https://github.com/NikolayKuts/LoKdroid/blob/maven_central/LICENSE.md) file for more details.

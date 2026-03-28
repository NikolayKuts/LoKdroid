# LoKdroid

[![Maven Central](https://img.shields.io/maven-central/v/io.github.nikolaykuts/lokdroid)](https://central.sonatype.com/artifact/io.github.nikolaykuts/lokdroid)

LoKdroid is a Kotlin Multiplatform logging library with a shared core API and Android-specific extensions for file and Logcat-oriented logging.

## Features

- **Shared logging API**: Use the same `LoKdroid`, log functions, and builder DSL from common code.
- **Multiplatform targets**: Core logging works on Android, desktop JVM, and iOS.
- **Customizable formatting and tagging**: Override formatter, logger, tag provider, and message builder factory.
- **Android-specific extensions**: File logging, Logcat-formatted files, and formatter DSL remain available on Android.

## Documentation
View **[KDoc](https://nikolaykuts.github.io/LoKdroid/)**

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
    implementation("io.github.nikolaykuts:lokdroid:0.0.5-alpha")
}
```

**Groovy**
```gradle
dependencies {
    implementation 'io.github.nikolaykuts:lokdroid:0.0.5-alpha'
}
```

For a Kotlin Multiplatform project, put the dependency in `commonMain` when you need the shared API there. Android-specific implementations such as `FileLogger`, `ConsoleAndFileLogger`, `RemoteLogger`, `FormatterBuilder`, and `FileFormat` are available only from `androidMain`.

Initialize LoKdroid before logging starts:

```kotlin
LoKdroid.initialize()
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

### Android Formatter Builder

On Android, you can configure how a single-line log message is formatted by chaining steps with `FormatterBuilder` and passing the built `IFormatter` into `LoKdroid.initialize`.

```kotlin
LoKdroid.initialize(
    formatter = FormatterBuilder()
        .withPointer()        // adds "--->"
        .space()              // adds a space
        .withLineReference()  // adds clickable File.kt:123 (Android Studio navigable)
        .space()
        .message()            // MANDATORY: injects your original log message
        .space()
        .custom(text = "some custom text") // appends any custom text
        .build()
)
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

            override operator fun String.invoke() {
                // use this block to build multiple message lines
            }
        }
    }
)
```

Platform visibility:
- `commonMain` sees the shared API, including `LoKdroid`, log functions, `ILogger`, `IFormatter`, `IMessageBuilder`, and `ConsoleLogger`.
- `androidMain` also sees Android-only implementations such as `FileLogger`, `ConsoleAndFileLogger`, `RemoteLogger`, and `FormatterBuilder`.

## License

This project is licensed under the Apache License, Version 2.0. See the [LICENSE](https://github.com/NikolayKuts/LoKdroid/blob/maven_central/LICENSE.md) file for more details.

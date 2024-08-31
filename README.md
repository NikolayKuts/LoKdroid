# LoKdroid

[![Maven Central](https://img.shields.io/maven-central/v/io.github.nikolaykuts/lokdroid)](https://central.sonatype.com/artifact/io.github.nikolaykuts/lokdroid)

LoKdroid is a comprehensive logging library designed for Android development, offering a flexible and customizable solution for managing logs across your Android applications.

## Features

- **Customizable Logging**: Provides options to define custom log formats, tags, and builders.
- **Default Implementations**: Comes with default implementations for log formatting, tag generation, and log building to simplify the setup process.
- **Android Studio Logcat import format support**: Supports saving logs in a format suitable for importing into Android Studio Logcat.

## Documentation
View **[KDoc](https://nikolaykuts.github.io/LoKdroid/)**

## Getting Started

To integrate LoKdroid into your Android project, follow these steps:

### 1. Add the Library to Your Project

Include the library in your project by adding it to your dependencies:

```gradle
..

repositories {
    mavenCentral()
}
```

**Kotlin DSL**
```gradle
..

dependencies {
    implementation("io.github.nikolaykuts:lokdroid:0.0.3-alpha")
}
```

**Groovy**
```gradle
..

dependencies {
    implementation 'io.github.nikolaykuts:lokdroid:0.0.3-alpha'
}
```

### 2. Initialize the Library

Initialize LoKdroid in your `Application` class:

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        LoKdroid.initialize()
    }
}
```
The initialization function must be called before logging begins.

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


### Multiple logging via LogBuilder

```kotlin
..

val year: Int = currentDate.year
val month: Int = currentDate.monthValue
val dayOfWeek: DayOfWeek = currentDate.dayOfWeek

logI {
    message("Date:")       // title
    message("Year: $year")
    message("Month: $month")
    message("Day of the Week: $dayOfWeek")
}
```
#### Output

<img width="1545" alt="image" src="https://github.com/user-attachments/assets/1469f691-b73b-437e-b7e0-f6a2d2dcda1a">

## Default implementations

#### Default initialising
```kotlin
fun initialize(
    minLevel: Level = Level.Verbose,
    logger: Logger = ConsoleLogger,
    formatter: Formatter = DefaultFormatter,
    tagProvider: () -> String = { DefaultTagProvider.getTag() },
    logBuilderProvider: LogBuilderProvider = DefaultLogBuilderProvider()
)
```
#### There are several implementations for targeted logging.
* _**ConsoleLogger**_ - logs messages to the Android console.
* _**FileLogger**_ - writes log messages to a specified file. *Supports a format suitable for import through Android Studio.*
* _**ConsoleAndFileLogger**_ - writes messages to both a file and the console.
* _**RemoteLogger**_ - sends log messages to a remote server.

## Customising
```kotlin
/** custom implementation */

LoKdroid.initialize(
    minLevel = Level.Debug,
    logger = { level: Level, tag: String, message: String -> /** your logic */ },
    formatter = { message -> "return formatted message: $message" },
    tagProvider = { "custom tag" },
    logBuilderProvider = {
        /** provide your custom LogBuilder */
        object : LogBuilder {
            override fun build(): String {
                return "build your string"
            }
            override fun message(value: Any) {
                /** use this block to build multiple log */
            }
        }
    }
)
```
For trying customising, check the [`MainActivity.kt`](https://github.com/NikolayKuts/LoKdroid/blob/development/sample/src/main/java/com/lib/lokdroid/MainActivity.kt) file in the `sample` module.

## License

This project is licensed under the Apache License, Version 2.0. See the [LICENSE](https://github.com/NikolayKuts/LoKdroid/blob/maven_central/LICENSE.md) file for more details.

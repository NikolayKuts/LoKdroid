package com.lib.lokdroid.core

import com.lib.lokdroid.domain.LogBuilder
import com.lib.lokdroid.domain.model.Level

/**
 * Logs a message at the specified level.
 *
 * @param message The message to be logged.
 * @param level The severity level at which to log the message; defaults to [LoKdroid.minLevel].
 */

fun log(message: String, level: Level = LoKdroid.minLevel) {
    LoKdroid.log(level = level, message = message)
}

/**
 * Logs a message at the [Level.Verbose] level.
 *
 * @param message The message to be logged. The message can be of any type that has a sensible string representation.
 */

fun logV(message: Any) {
    LoKdroid.log(level = Level.Verbose, message = message.toString())
}

/**
 * Logs a message at the [Level.Debug] level.
 *
 * @param message The message to be logged. The message can be of any type that has a sensible string representation.
 */

fun logD(message: Any) {
    LoKdroid.log(level = Level.Debug, message = message.toString())
}

/**
 * Logs a message at the [Level.Info] level.
 *
 * @param message The message to be logged. The message can be of any type that has a sensible string representation.
 */

fun logI(message: Any) {
    LoKdroid.log(level = Level.Info, message = message.toString())
}

/**
 * Logs a message at the [Level.Warn] level.
 *
 * @param message The message to be logged. The message can be of any type that has a sensible string representation.
 */

fun logW(message: Any) {
    LoKdroid.log(level = Level.Warn, message = message.toString())
}

/**
 * Logs a message at the [Level.Error] level.
 *
 * @param message The message to be logged. The message can be of any type that has a sensible string representation.
 */

fun logE(message: Any) {
    LoKdroid.log(level = Level.Error, message = message.toString())
}

/**
 * Logs a complex message constructed via [LogBuilder] at the specified level.
 * This method allows for more complex and dynamic log message constructions.
 *
 * @param level The severity level of the log; defaults to [LoKdroid.minLevel].
 * @param block A lambda block that applies configurations to the [LogBuilder] instance.
 */

fun log(level: Level = LoKdroid.minLevel, block: LogBuilder.() -> Unit) {
    LoKdroid.log(level = level, block = block)
}

/**
 * Logs a complex message at the [Level.Verbose] using a [LogBuilder].
 *
 * @param block A lambda block that applies configurations to the [LogBuilder] instance.
 */

fun logV(block: LogBuilder.() -> Unit) {
    LoKdroid.log(level = Level.Verbose, block = block)
}

/**
 * Logs a complex message at the [Level.Debug] using a [LogBuilder].
 *
 * @param block A lambda block that applies configurations to the [LogBuilder] instance.
 */

fun logD(block: LogBuilder.() -> Unit) {
    LoKdroid.log(level = Level.Debug, block = block)
}

/**
 * Logs a complex message at the [Level.Info] using a [LogBuilder].
 *
 * @param block A lambda block that applies configurations to the [LogBuilder] instance.
 */

fun logI(block: LogBuilder.() -> Unit) {
    LoKdroid.log(level = Level.Info, block = block)
}

/**
 * Logs a complex message at the [Level.Warn] using a [LogBuilder].
 *
 * @param block A lambda block that applies configurations to the [LogBuilder] instance.
 */

fun logW(block: LogBuilder.() -> Unit) {
    LoKdroid.log(level = Level.Warn, block = block)
}

/**
 * Logs a complex message at the [Level.Error] using a [LogBuilder].
 *
 * @param block A lambda block that applies configurations to the [LogBuilder] instance.
 */

fun logE(block: LogBuilder.() -> Unit) {
    LoKdroid.log(level = Level.Error, block = block)
}
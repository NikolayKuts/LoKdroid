package com.lib.lokdroid.data.default_implementation.logger

import android.util.Log
import com.lib.lokdroid.domain.Logger
import com.lib.lokdroid.domain.model.Level

/**
 * Implementation of the [Logger] interface that logs messages to the Android console using [Log].
 *
 * This logger converts logging levels into Android's log priority constants and outputs messages
 * to the system's log buffer, typically viewable via Logcat.
 */

object ConsoleLogger : Logger {

    /**
     * Logs a message to the console with the specified logging level, tag, and message.
     *
     * This method adapts the generic logging interface to Android's logging system by translating
     * the [Level] to a corresponding Android log priority.
     *
     * @param level The severity level of the log, which determines the priority of the message.
     * @param tag The tag associated with the log message, generally used for categorizing logs.
     * @param message The content of the log message to output.
     */

    override fun log(
        level: Level,
        tag: String,
        message: String,
    ) {
        Log.println(getPriority(level = level), tag, message)
    }

    /**
     * Converts a [Level] to the corresponding Android log priority integer.
     *
     * This method ensures that logs are categorized correctly in the Android logging system,
     * facilitating easier filtering and analysis in tools like Logcat.
     *
     * @param level The [Level] to be converted.
     * @return The Android log priority as an integer.
     */

    private fun getPriority(level: Level): Int = when (level) {
        Level.Verbose -> Log.VERBOSE
        Level.Debug -> Log.DEBUG
        Level.Info -> Log.INFO
        Level.Warn -> Log.WARN
        Level.Error -> Log.ERROR
    }
}
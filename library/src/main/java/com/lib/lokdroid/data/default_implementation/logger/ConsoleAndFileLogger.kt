package com.lib.lokdroid.data.default_implementation.logger

import com.lib.lokdroid.domain.Logger
import com.lib.lokdroid.domain.model.Level

/**
 * A logger that outputs messages to both a file and the console.
 *
 * This class is designed to facilitate logging across two mediums simultaneously, leveraging
 * a [FileLogger] for file output and [ConsoleLogger] for console output. This is particularly
 * useful for environments where persistence and immediate visibility of logs are required.
 *
 * @param fileLogger An instance of [FileLogger] used to log messages to a file.
 */

class ConsoleAndFileLogger(
    private val fileLogger: FileLogger,
) : Logger {

    /**
     * Logs a message to both the file and console with the specified level, tag, and message.
     *
     * This method delegates the logging action to both a [FileLogger] and [ConsoleLogger],
     * ensuring that the message is recorded in the log file and displayed on the console.
     * It is useful for situations where logs need to be both persistently stored and immediately observed.
     *
     * @param level The severity level of the log message.
     * @param tag A string tag used to categorize the log message.
     * @param message The text of the log message.
     */

    override fun log(level: Level, tag: String, message: String) {
        fileLogger.log(level = level, tag = tag, message = message)
        ConsoleLogger.log(level = level, tag = tag, message = message)
    }
}
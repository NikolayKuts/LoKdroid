package com.lib.lokdroid.data.default_implementation.logger

import com.lib.lokdroid.data.default_implementation.toEmoji
import com.lib.lokdroid.domain.ILogger
import com.lib.lokdroid.domain.model.Level

/**
 * iOS console logger that writes formatted messages to standard output for Xcode console viewing.
 */
actual object ConsoleLogger : ILogger {

    /**
     * Writes an iOS log entry using the shared `Tag [Level] emoji message` format.
     */
    actual override fun log(
        level: Level,
        tag: String,
        message: String
    ) {
        println("$tag\t[$level] ${level.toEmoji()} $message")
    }
}

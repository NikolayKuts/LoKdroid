package com.lib.lokdroid.data.default_implementation.logger

import com.lib.lokdroid.data.default_implementation.toEmoji
import com.lib.lokdroid.domain.ILogger
import com.lib.lokdroid.domain.model.Level

/**
 * Web console logger that writes messages to the browser console.
 */
actual object ConsoleLogger : ILogger {

    actual override fun log(
        level: Level,
        tag: String,
        message: String,
    ) {
        println("$tag\t[$level] ${level.toEmoji()} $message")
    }
}

package com.lib.lokdroid.data.default_implementation.logger

import com.lib.lokdroid.data.default_implementation.toEmoji
import com.lib.lokdroid.domain.ILogger
import com.lib.lokdroid.domain.model.Level

/**
 * Web console logger that writes messages to the browser console.
 *
 * Each level is routed to the matching console method so that browser developer tools apply their own
 * severity styling and level filters to LoKdroid output.
 */
actual object ConsoleLogger : ILogger {

    actual override fun log(
        level: Level,
        tag: String,
        message: String,
    ) {
        val renderedMessage = "$tag\t[$level] ${level.toEmoji()} $message"

        when (level) {
            Level.Verbose, Level.Debug -> console.log(renderedMessage)
            Level.Info -> console.info(renderedMessage)
            Level.Warn -> console.warn(renderedMessage)
            Level.Error -> console.error(renderedMessage)
        }
    }
}

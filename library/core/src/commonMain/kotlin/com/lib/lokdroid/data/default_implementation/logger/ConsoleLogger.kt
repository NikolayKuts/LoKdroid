package com.lib.lokdroid.data.default_implementation.logger

import com.lib.lokdroid.domain.ILogger
import com.lib.lokdroid.domain.model.Level

/**
 * Default platform console logger implementation used when no custom [ILogger] is supplied.
 */
expect object ConsoleLogger : ILogger {

    /**
     * Logs a message using the platform-native console mechanism.
     */
    override fun log(level: Level, tag: String, message: String)
}

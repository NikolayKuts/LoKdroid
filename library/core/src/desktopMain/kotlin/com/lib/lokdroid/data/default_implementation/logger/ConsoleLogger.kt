package com.lib.lokdroid.data.default_implementation.logger

import com.lib.lokdroid.domain.ILogger
import com.lib.lokdroid.domain.model.Level

actual object ConsoleLogger : ILogger {

    actual override fun log(level: Level, tag: String, message: String) {
        println("[LoKdroid][$level][$tag] $message")
    }
}

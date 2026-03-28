package com.lib.lokdroid.data.default_implementation.logger

import com.lib.lokdroid.domain.ILogger
import com.lib.lokdroid.domain.model.Level

expect object ConsoleLogger : ILogger {
    override fun log(level: Level, tag: String, message: String)
}

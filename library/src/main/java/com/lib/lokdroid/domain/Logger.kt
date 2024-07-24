package com.lib.lokdroid.domain

import com.lib.lokdroid.domain.model.Level

/**
 * A functional interface for logging messages.
 *
 * This interface allows implementing custom logging logic that can be passed around as lambdas
 * and can be used with various logging frameworks.
 */

fun interface Logger {

    /**
     * Logs a message with the specified [level], [tag], and [message].
     *
     * @param level The level of the log message.
     * @param tag The tag associated with the log message.
     * @param message The actual message to be logged.
     */

    fun log(level: Level, tag: String, message: String)
}
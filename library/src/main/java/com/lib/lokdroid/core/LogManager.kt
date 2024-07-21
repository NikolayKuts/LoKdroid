package com.lib.lokdroid.core

import com.lib.lokdroid.domain.Formatter
import com.lib.lokdroid.domain.LogBuilder
import com.lib.lokdroid.domain.LogBuilderProvider
import com.lib.lokdroid.domain.Logger
import com.lib.lokdroid.domain.model.Level

/**
 * Manages logging operations by filtering based on log level, formatting messages,
 * and delegating the logging to a specific logger implementation.
 *
 * @property minLevel The minimum logging level that this manager will process. Logs below this level will be ignored.
 * @property logger The logger that performs the actual logging of the formatted messages.
 * @property formatter The formatter used to format log messages before they are logged.
 * @property tag A tag associated with all logs generated by this manager, typically used to categorize logs.
 * @property logBuilderProvider A provider that supplies [LogBuilder] instances for constructing complex log messages.
 */

internal class LogManager(
    val minLevel: Level,
    val logger: Logger,
    private val formatter: Formatter,
    private val tag: String,
    private val logBuilderProvider: LogBuilderProvider,
) {

    /**
     * Logs a message at the specified level if the level is above or equal to [minLevel].
     *
     * @param level The severity level of the log.
     * @param message The message to log.
     */

    fun log(level: Level, message: String) {
        level.performByMinLevel {
            logger.log(
                level = level,
                tag = tag,
                message = formatter.format(message)
            )
        }
    }

    /**
     * Logs a complex message constructed via [LogBuilder] at the specified level if the level is above or equal to [minLevel].
     * This method allows for more complex and dynamic log message constructions.
     *
     * @param level The severity level of the log.
     * @param block A lambda block that applies configurations to the [LogBuilder] instance.
     */

    fun log(level: Level, block: LogBuilder.() -> Unit) {
        level.performByMinLevel {
            val logBuilder = logBuilderProvider.provide()

            block(logBuilder)

            logger.log(
                level = level,
                tag = tag,
                message = formatter.format(logBuilder.build())
            )
        }
    }

    /**
     * Helper method that executes the provided block if the current [Level] ordinal is greater than or equal to the [minLevel] ordinal.
     *
     * @param block The block of code to execute if the logging level check passes.
     */

    private inline fun Level.performByMinLevel(block: () -> Unit) {
        if (ordinal >= minLevel.ordinal) block()
    }
}
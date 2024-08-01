package com.lib.lokdroid.data.default_implementation.logger.logcat.entities

import kotlinx.serialization.Serializable

/**
 * Represents a logcat message with a header and a message body.
 *
 * @property header The header of the log message containing metadata.
 * @property message The actual log message content.
 */
@Serializable
internal data class LogcatMessage(
    val header: LogHeader,
    val message: String
)
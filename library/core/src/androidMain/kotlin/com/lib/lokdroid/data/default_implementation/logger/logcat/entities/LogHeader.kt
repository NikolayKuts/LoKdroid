package com.lib.lokdroid.data.default_implementation.logger.logcat.entities

import kotlinx.serialization.Serializable

/**
 * Represents the header of a log message, containing various metadata fields.
 *
 * @property logLevel The level of the log message.
 * @property pid The process ID.
 * @property tid The thread ID.
 * @property applicationId The application ID.
 * @property processName The name of the process.
 * @property tag The tag associated with the log message.
 * @property timestamp The timestamp of the log message.
 */
@Serializable
internal data class LogHeader(
    val logLevel: String,
    val pid: Int,
    val tid: Int,
    val applicationId: String,
    val processName: String,
    val tag: String,
    val timestamp: Timestamp
)
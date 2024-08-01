package com.lib.lokdroid.data.default_implementation.logger.logcat.entities

import kotlinx.serialization.Serializable

/**
 * Represents the content of a logcat log, containing a list of logcat messages.
 *
 * @property logcatMessages The list of logcat messages. Defaults to an empty list.
 */
@Serializable
internal data class LogcatContent(
    val logcatMessages: List<LogcatMessage> = emptyList()
)
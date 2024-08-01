package com.lib.lokdroid.data.default_implementation.logger.logcat.entities

import kotlinx.serialization.Serializable

/**
 * Represents a timestamp with seconds and nanoseconds precision.
 *
 * @property seconds The number of seconds since the epoch.
 * @property nanos The number of nanoseconds past the seconds.
 */
@Serializable
internal data class Timestamp(
    val seconds: Long,
    val nanos: Int
)
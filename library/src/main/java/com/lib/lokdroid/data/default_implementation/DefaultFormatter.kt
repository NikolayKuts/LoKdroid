package com.lib.lokdroid.data.default_implementation

import com.lib.lokdroid.domain.Formatter

/**
 * Implements the [Formatter] interface to provide a simple and consistent message formatting.
 *
 * This formatter prepends a pointer string to each message to highlight it in the log output,
 * making it easier to distinguish log messages visually when reviewing logs.
 *
 * @property POINTER Constant pointer symbol used in formatting.
 */

object DefaultFormatter : Formatter {

    private const val POINTER = "--->"

    /**
     * Formats the provided message by prepending a pointer to it.
     *
     * This method enhances message visibility in logs by adding a specific prefix,
     * which can be particularly useful in cluttered log environments where messages
     * need to stand out for quick scanning.
     *
     * @param message The raw message to be formatted.
     * @return The formatted message with a pointer prefixed.
     */

    override fun format(message: String): String {
        return "$POINTER $message"
    }
}
package com.lib.lokdroid.data.default_implementation

import com.lib.lokdroid.domain.ILogBuilder

/**
 * Default implementation of [ILogBuilder] providing a simple DSL-based log composition.
 *
 * Each string literal invoked as a function inside a logging block is appended as a new line.
 * The very first line is treated as a header, subsequent lines are prefixed with "->" for readability.
 */

class LogBuilder : ILogBuilder {

    private val contentBuilder = StringBuilder()

    override fun String.invoke() {
        if (contentBuilder.isEmpty()) {
            contentBuilder.append(this)
        } else {
            contentBuilder.append("\n\t-> $this")
        }
    }

    /**
     * Builds and returns the final log message combined from all lines.
     *
     * @return The complete log message as a single formatted string.
     */

    override fun build(): String = contentBuilder.toString()
}
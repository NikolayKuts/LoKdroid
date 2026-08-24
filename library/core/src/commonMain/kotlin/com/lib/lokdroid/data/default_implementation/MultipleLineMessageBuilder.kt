package com.lib.lokdroid.data.default_implementation

import com.lib.lokdroid.domain.IMessageBuilder
import com.lib.lokdroid.domain.model.Level

/**
 * Default implementation of [IMessageBuilder] for building a multi-line message with the DSL.
 *
 * Each string literal invoked as a function inside a logging block is appended as a new line.
 * The very first line is treated as a header, while subsequent lines are prefixed with "->" for readability.
 */
class MultipleLineMessageBuilder : IMessageBuilder {

    private val contentBuilder = StringBuilder()

    override fun String.invoke(level: Level?) {
        if (contentBuilder.isEmpty()) {
            contentBuilder.append(this)
        } else {
            contentBuilder.append("\n\t${level.toEmojiOrEmpty()} -> $this")
        }
    }

    /**
     * Builds and returns the final message combined from all lines.
     *
     * @return The complete message as a single formatted string.
     */

    override fun build(): String = contentBuilder.toString()
}

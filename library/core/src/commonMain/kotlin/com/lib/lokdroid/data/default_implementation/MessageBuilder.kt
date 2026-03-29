package com.lib.lokdroid.data.default_implementation

import com.lib.lokdroid.domain.IMessageBuilder
import com.lib.lokdroid.domain.model.Level

/**
 * Default implementation of [IMessageBuilder] providing a simple DSL-based message composition.
 *
 * Each string literal invoked as a function inside a logging block is appended as a new line.
 * The very first line is treated as a header, subsequent lines are prefixed with "->" for readability.
 */

class MessageBuilder : IMessageBuilder {

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

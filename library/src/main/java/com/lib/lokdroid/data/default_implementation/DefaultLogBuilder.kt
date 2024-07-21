package com.lib.lokdroid.data.default_implementation

import com.lib.lokdroid.domain.LogBuilder

/**
 * Implements the [LogBuilder] interface providing a simple log building mechanism.
 *
 * This class uses two [StringBuilder]s to construct a log message consisting of a title and content.
 * The first message passed to [message] will be considered the title, and subsequent messages
 * will be treated as content, appended under the title in a list format.
 */

class DefaultLogBuilder : LogBuilder {

    private val titleBuilder = StringBuilder()
    private val contentBuilder = StringBuilder()


    /**
     * Appends the given value to the log message. If no title has been set, the first
     * value becomes the title. Any subsequent values are appended as content.
     *
     * @param value The message component to add to the log. The value is converted to a string
     * and either becomes the title or part of the content.
     */

    override fun message(value: Any) {
        if (titleBuilder.isEmpty()) {
            titleBuilder.append(value)
        } else {
            contentBuilder.append("\n\t-> $value")
        }
    }

    /**
     * Builds and returns the final log message, combining the title and content.
     *
     * @return The complete log message formatted with a title and list of content entries.
     */

    override fun build(): String = titleBuilder.toString() + contentBuilder.toString()
}
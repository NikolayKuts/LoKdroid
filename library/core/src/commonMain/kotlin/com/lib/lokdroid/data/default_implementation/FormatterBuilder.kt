package com.lib.lokdroid.data.default_implementation

import com.lib.lokdroid.domain.IFormatter

/**
 * A builder class for constructing custom formatted log messages using a simple chain DSL.
 *
 * The [FormatterBuilder] lets you compose the final log output by chaining steps in the desired
 * order: e.g. pointer, spaces, line reference, the actual message, and any custom text.
 * Important: the original message is not added automatically. You must call [message] somewhere
 * in the chain, otherwise the resulting formatted text will not contain your message.
 *
 * Available steps:
 * - [withPointer] adds a pointer string (default: `--->`).
 * - [space] adds a single space character.
 * - [withLineReference] adds a platform-specific caller reference.
 * - [message] injects the original message text.
 * - [custom] adds any custom text.
 */
class FormatterBuilder {

    /** The message currently being formatted. */
    private var logMessage: String = ""
    /** Ordered formatting operations applied to the message during [build]. */
    private val formattingTasks: MutableList<() -> String> = mutableListOf()

    /**
     * Builds an [IFormatter] that applies all configured formatting operations to each message.
     *
     * @return A formatter composed from the tasks added to this builder.
     */
    fun build(): IFormatter = IFormatter { message ->
        logMessage = message

        buildString {
            formattingTasks.forEach { task -> append(task.invoke()) }
        }
    }

    /**
     * Adds the original log message to the output sequence.
     *
     * @return The current builder instance.
     */
    fun message(): FormatterBuilder = this.apply {
        addFormattingTask { logMessage }
    }

    /**
     * Adds a platform-specific caller reference to the output sequence.
     *
     * On Android this is typically a `FileName:LineNumber` reference.
     * On desktop and iOS this is rendered as a stack-frame-like suffix.
     *
     * @return The current builder instance.
     */
    fun withLineReference(): FormatterBuilder = this.apply {
        addFormattingTask { getFormattedLineReference() }
    }

    /**
     * Adds a pointer marker to the output sequence.
     *
     * @param pointer The pointer text to append. Defaults to `--->`.
     * @return The current builder instance.
     */
    fun withPointer(pointer: String = "--->"): FormatterBuilder = this.apply {
        addFormattingTask { pointer }
    }

    /**
     * Adds a single space character to the output sequence.
     *
     * @return The current builder instance.
     */
    fun space(): FormatterBuilder = this.apply {
        addFormattingTask { " " }
    }

    /**
     * Adds custom text to the output sequence.
     *
     * @param text The text to append.
     * @return The current builder instance.
     */
    fun custom(text: String): FormatterBuilder = this.apply {
        addFormattingTask { text }
    }

    /**
     * Registers a formatting task to be executed when the formatter runs.
     *
     * @param block The task that produces a text fragment.
     */
    private fun addFormattingTask(block: () -> String) {
        formattingTasks.add(block)
    }
}

/**
 * Returns the line-reference string used by [FormatterBuilder.withLineReference] on the current platform.
 */
internal expect fun getFormattedLineReference(): String

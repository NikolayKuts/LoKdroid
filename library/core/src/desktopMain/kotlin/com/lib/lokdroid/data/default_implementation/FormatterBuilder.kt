package com.lib.lokdroid.data.default_implementation

import com.lib.lokdroid.domain.IFormatter

/**
 * Desktop formatter builder with the same DSL as Android.
 *
 * On desktop, [withLineReference] is rendered as a dedicated stack-frame line appended to the end
 * of the formatted message so IntelliJ-based IDE consoles can navigate to the call site.
 */
class FormatterBuilder {

    /** The log message to be formatted */
    private var logMessage: String = ""
    /** List of tasks to build formatted message */
    private val formattingTasks: MutableList<() -> String> = mutableListOf()
    /** Number of line reference tasks requested by the builder */

    /**
     * Builds and returns a [IFormatter] that applies all the formatting tasks
     * defined in the builder to the given log message.
     *
     * @return A [IFormatter] instance that formats the message according to the added tasks.
     */
    fun build(): IFormatter = IFormatter { message ->
        logMessage = message

        buildString {
            formattingTasks.forEach { task -> append(task.invoke()) }
        }
    }

    /**
     * Adds the original log message to the formatting sequence.
     *
     * @return The current [FormatterBuilder] instance for chaining.
     */
    fun message(): FormatterBuilder = this.apply {
        addFormattingTask { logMessage }
    }

    /**
     * Appends a clickable desktop stack-frame reference to the formatted output.
     * The reference is emitted as a dedicated trailing line so the same builder chain remains readable.
     *
     * @return The current [FormatterBuilder] instance for chaining.
     */
    fun withLineReference(): FormatterBuilder = this.apply {
        addFormattingTask { buildLineReferencesBlock() }
    }

    /**
     * Adds a pointer string (default is "--->") to the formatting sequence.
     *
     * @param pointer The custom pointer string to use (default: "--->").
     * @return The current [FormatterBuilder] instance for chaining.
     */
    fun withPointer(pointer: String = "--->"): FormatterBuilder = this.apply {
        addFormattingTask { pointer }
    }

    /**
     * Adds a space to the formatting sequence.
     *
     * @return The current [FormatterBuilder] instance for chaining.
     */
    fun space(): FormatterBuilder = this.apply {

        addFormattingTask { " " }
    }

    /**
     * Adds custom text to the formatting sequence.
     *
     * @param text The custom text to add.
     * @return The current [FormatterBuilder] instance for chaining.
     */
    fun custom(text: String): FormatterBuilder = this.apply {
        addFormattingTask { text }
    }

    private fun buildLineReferencesBlock(): String = buildString {
        append(getClickableLineReference())
    }

    /**
     * Adds a formatting task to the list of tasks.
     *
     * @param block A lambda function that returns the string to be appended to the formatted message.
     */
    private fun addFormattingTask(block: () -> String) {
        formattingTasks.add(block)
    }
}

package com.lib.lokdroid.data.default_implementation

import com.lib.lokdroid.domain.Formatter

/**
 * A builder class for constructing custom formatted log messages.
 *
 * The [FormaterBuilder] allows you to specify various formatting tasks such as
 * including the original message, adding a line reference, a pointer, or custom text.
 * Once all formatting tasks are added, the final `Formatter` object is built,
 * which applies these tasks to format the log message when invoked.
 */
class FormaterBuilder {

    /** The log message to be formatted */
    private var logMessage: String = ""
    /** List of tasks to build formatted message */
    private val formatingTasks: MutableList<() -> String> = mutableListOf()

    /**
     * Builds and returns a [Formatter] that applies all the formatting tasks
     * defined in the builder to the given log message.
     *
     * @return A [Formatter] instance that formats the message according to the added tasks.
     */
    fun build(): Formatter = Formatter { message ->
        logMessage = message

        buildString {
            formatingTasks.forEach { task -> append(task.invoke()) }
        }
    }
    /**
     * Adds the original log message to the formatting sequence.
     *
     * @return The current [FormaterBuilder] instance for chaining.
     */
    fun message(): FormaterBuilder = this.apply {
        addFormattingTask { logMessage }
    }

    /**
     * "Appends a code line reference (file name and line number) to the formatting sequence,
     * enabling navigation to the specific line of code where the log was invoked.
     *
     * @return The current [FormaterBuilder] instance for chaining.
     */
    fun withLineReference(): FormaterBuilder = this.apply {
        addFormattingTask { getLineReference() }
    }

    /**
     * Adds a pointer string (default is "--->") to the formatting sequence.
     *
     * @param pointer The custom pointer string to use (default: "--->").
     * @return The current [FormaterBuilder] instance for chaining.
     */
    fun withPointer(pointer: String = "--->"): FormaterBuilder = this.apply {
        addFormattingTask { pointer }
    }

    /**
     * Adds a space to the formatting sequence.
     *
     * @return The current [FormaterBuilder] instance for chaining.
     */
    fun space(): FormaterBuilder = this.apply {
        addFormattingTask { " " }
    }

    /**
     * Adds custom text to the formatting sequence.
     *
     * @param text The custom text to add.
     * @return The current [FormaterBuilder] instance for chaining.
     */
    fun custom(text: String): FormaterBuilder = this.apply {
        addFormattingTask { text }
    }

    /**
     * Adds a formatting task to the list of tasks.
     *
     * @param block A lambda function that returns the string to be appended to the formatted message.
     */
    private fun addFormattingTask(block: () -> String) {
        formatingTasks.add(block)
    }

    /**
     * Retrieves the line reference of the code that called the logger.
     *
     * @return A string in the format "FileName:LineNumber" representing the file and line of the caller
     * or "????" if the reference is not available
     */
    private fun getLineReference(): String {
        val referenceElement = getTargetReferenceStackTraceElement() ?: return "????"
        val fileName = referenceElement.fileName
        val lineNumber = referenceElement.lineNumber
        return "$fileName:$lineNumber"
    }
}
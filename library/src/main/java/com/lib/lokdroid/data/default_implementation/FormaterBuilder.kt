package com.lib.lokdroid.data.default_implementation

import com.lib.lokdroid.domain.Formatter

/**
 * A builder class for constructing custom formatted log messages using a simple chain DSL.
 *
 * The [FormaterBuilder] lets you compose the final log output by chaining steps in the desired
 * order: e.g. pointer, spaces, clickable line reference, the actual message, and any custom text.
 * Important: the original message is NOT added automatically — you MUST call [message] somewhere
 * in the chain, otherwise the resulting formatted text will not contain your message.
 *
 * Available steps:
 * - [withPointer] — adds a pointer string (default: `--->`).
 * - [space] — adds a single space character.
 * - [withLineReference] — adds a clickable `FileName:LineNumber` reference recognized by Android Studio.
 * - [message] — injects the original message text (MANDATORY if you want the message to appear).
 * - [custom] — adds any custom text.
 *
 * Usage example:
 *
 * val formatter = FormaterBuilder()
 *     .withPointer()
 *     .space()
 *     .withLineReference()
 *     .space()
 *     .message() // mandatory to include the message content
 *     .space()
 *     .custom(text = "some custom text")
 *     .build()
 *
 * // Later in initialize():
 * // LoKdroid.initialize(formatter = formatter)
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
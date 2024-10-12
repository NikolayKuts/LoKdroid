package com.lib.lokdroid.data.default_implementation

import android.util.Log
import com.lib.lokdroid.core.LoKdroid
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val LOG_DATE_PATTERN = "dd-MM-yyyy HH:mm:ss.SSS"

/**
 * Formats a given timestamp into a string based on the specified date pattern.
 *
 * @param timestamp The timestamp in milliseconds to be formatted.
 * @param pattern The pattern to be used for formatting the date. Defaults to [LOG_DATE_PATTERN].
 * @return A formatted date string.
 *
 * @throws IllegalArgumentException If the given pattern is invalid.
 */
internal fun formatDate(timestamp: Long, pattern: String = LOG_DATE_PATTERN): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(Date(timestamp))
}

/**
 * Extension function for [StackTraceElement] that retrieves the short class name.
 *
 * The short class name is the last part of the fully qualified class name (after the last dot).
 * @return The short name of the class from the [StackTraceElement].
 */
internal fun StackTraceElement.getShortClassName(): String {
    return className.split('.').last().split("$").first()
}


/**
 * Extension function for [StackTraceElement] that retrieves the long class name.
 *
 * The long class name is the last part of the fully qualified class name (after the last dot), but
 * it removes any numeric identifiers of anonymous or lambda classes (e.g., `$1`, `$2`).
 *
 * @return The long class name with any `$` and numeric suffixes removed.
 */
internal fun StackTraceElement.getLongClassName(): String {
    return className.split('.')
        .last()
        .replace(
            regex = "(\\$\\d+)+".toRegex(),
            replacement = ""
        )
}

/**
 * Retrieves the relevant [StackTraceElement] that references the point in code where the logger was called.
 *
 * This method looks through the current thread's stack trace to find the last occurrence of the class
 * related to the logger ([LoKdroid] class), then returns the element that is two steps ahead of that
 * occurrence. This is typically the location in the code where the logging actually took place.
 *
 * @return The [StackTraceElement] pointing to the code reference, or `null` if it cannot be determined.
 */
internal fun getTargetReferenceStackTraceElement(): StackTraceElement? {
    val stackTrace = Thread.currentThread().stackTrace
    val stackTraceElement = stackTrace.lastOrNull {
        it.className.contains(LoKdroid::class.simpleName ?: "UnknownClass")
    }
    val stackTraceElementIndex = stackTrace.indexOf(stackTraceElement)
    val stepToReferenceElement = 2
    val referenceElement = stackTrace[stackTraceElementIndex + stepToReferenceElement]

    return referenceElement
}

/**
 * Extension function for `Array<[StackTraceElement]?>?` that logs the contents of the stack trace.
 *
 * This method prints each element in the stack trace, along with its index,
 * to the Android lagcat using the provided `tag`.
 * If the stack trace array is `null`, it logs a message indicating that the stack trace is `null`.
 *
 * @param tag The tag to be used in the log output.
 */
internal fun Array<StackTraceElement?>?.printContent(tag: String) {
    if (this == null) {
        Log.d(tag,"stackTraceElements: null")
        return
    }

    val stackTraceContent = this.toList()
        .mapIndexed { index, stackTraceElement -> "\n> $index $stackTraceElement" }

    Log.d(tag, "stackTraceElements: - size: ${this.size} -> $stackTraceContent")
}
package com.lib.lokdroid.domain

/**
 * A functional interface for formatting strings.
 *
 * This interface provides a method to format a string according to specific rules or styles.
 * Implementations of this interface can be used to transform strings into a desired format,
 * such as applying text styles, correcting grammar, or adding specific prefixes or suffixes.
 */

fun interface Formatter {

    /**
    * Formats the given message and returns the formatted string.
    *
    * The method takes a raw string input and returns it in a modified format based on the
    * implementation's specific logic. The formatting could include operations like
    * trimming, appending, uppercasing, or any other textual manipulation.
    *
    * @param message The raw string to be formatted.
    * @return The formatted string.
    */

    fun format(message: String): String
}
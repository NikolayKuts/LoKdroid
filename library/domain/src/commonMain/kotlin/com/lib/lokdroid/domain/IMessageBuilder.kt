package com.lib.lokdroid.domain

import com.lib.lokdroid.domain.model.Level

/**
 * An interface for building messages using a simple DSL.
 *
 * Alongside [build] and the string invocation operator, this interface exposes shorthand
 * properties for commonly used log levels. These are intended to be passed into
 * [String.invoke] inside the builder block:
 *
 * ```kotlin
 * logI {
 *     "Header"()
 *     "Something failed"(E)
 *     "Extra details"(D)
 * }
 * ```
 */

interface IMessageBuilder {

    /** Shorthand for [Level.Verbose] when tagging a DSL line with the verbose level. */
    val V get() = Level.Verbose

    /** Shorthand for [Level.Debug] when tagging a DSL line with the debug level. */
    val D get() = Level.Debug

    /** Shorthand for [Level.Info] when tagging a DSL line with the info level. */
    val I get() = Level.Info

    /** Shorthand for [Level.Warn] when tagging a DSL line with the warning level. */
    val W get() = Level.Warn

    /** Shorthand for [Level.Error] when tagging a DSL line with the error level. */
    val E get() = Level.Error

    /**
     * Builds and returns the final message as a string.
     *
     * This method consolidates all previously provided message components
     * into a single string. The exact formatting and ordering of components in the resulting
     * string are determined by the specific implementation of this interface.
     *
     * @return The fully formatted message as a String.
     */
    fun build(): String

    /**
     * Adds a new message line to this builder via a DSL-style string invocation.
     *
     * @param level Optional line-specific log level. Implementations may use it to decorate
     * the line, for example with a severity emoji or another visual marker.
     */
    operator fun String.invoke(level: Level? = null)
}

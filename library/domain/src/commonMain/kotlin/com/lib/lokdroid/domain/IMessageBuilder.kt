package com.lib.lokdroid.domain

/**
 * An interface for building messages using a simple DSL.
 */

interface IMessageBuilder {

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
     */

    operator fun String.invoke()
}

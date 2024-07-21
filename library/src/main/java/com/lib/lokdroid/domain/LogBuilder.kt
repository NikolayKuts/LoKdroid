package com.lib.lokdroid.domain

/**
 * An interface for building log messages.
 *
 * This interface provides a structured way to assemble log messages from various components
 * or values, culminating in a final string representation of the log message.
 * Implementations of this interface should handle how messages are built to a string.
 */

interface LogBuilder {

    /**
     * Builds and returns the final log message as a string.
     *
     * This method consolidates all previously provided message components or values
     * into a single string. The exact formatting and ordering of components in the resulting
     * string are determined by the specific implementation of this interface.
     *
     * @return The fully formatted log message as a String.
     */

    fun build(): String

    /**
     * Adds a new message component to this log builder.
     *
     * This method accepts any object, incorporating it into the log message being built.
     * Depending on the implementation, this method might convert the object into a string
     * immediately, or it might store the object for later processing during the build.
     *
     * @param value The message component to be added. This can be any object whose
     *              string representation might be included in the final log message.
     */

    fun message(value: Any)
}
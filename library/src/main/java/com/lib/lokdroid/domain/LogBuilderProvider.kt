package com.lib.lokdroid.domain

/**
 * Provides a functional interface for obtaining instances of [LogBuilder].
 *
 * This interface defines a single method [provide] that is used to create new
 * instances of [LogBuilder] each time the logging function is called.
 */

fun interface LogBuilderProvider {

    /**
     * Creates and returns a new instance of [LogBuilder].
     *
     * The implementation of this method should provide a fully configured instance
     * of [LogBuilder] that is ready to be used for building log messages according
     * to specific requirements or configurations.
     *
     * @return a new instance of [LogBuilder].
     */

    fun provide(): LogBuilder
}
package com.lib.lokdroid.domain

/**
 * Provides a functional interface for obtaining instances of [ILogBuilder].
 *
 * This interface defines a single method [provide] that is used to create new
 * instances of [ILogBuilder] each time the logging function is called.
 */

fun interface ILogBuilderProvider {

    /**
     * Creates and returns a new instance of [ILogBuilder].
     *
     * The implementation of this method should provide a fully configured instance
     * of [ILogBuilder] that is ready to be used for building log messages according
     * to specific requirements or configurations.
     *
     * @return a new instance of [ILogBuilder].
     */

    fun provide(): ILogBuilder
}
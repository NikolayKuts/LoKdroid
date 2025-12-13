package com.lib.lokdroid.data.default_implementation

import com.lib.lokdroid.domain.ILogBuilderProvider

/**
 * Provides a default implementation of the [ILogBuilderProvider] interface by supplying instances of [LogBuilder].
 *
 * This class serves as a concrete provider for creating new [LogBuilder] instances, which are used
 * for constructing log messages in a structured format. It ensures that any logging system using this provider
 * gets a fresh instance of [LogBuilder] each time a log builder is requested.
 */

class LogBuilderProvider : ILogBuilderProvider {

    /**
     * Creates and returns a new instance of [LogBuilder].
     *
     * This method is a straightforward factory for [LogBuilder], ensuring that each call
     * provides a new, isolated instance of the log builder, avoiding unintended sharing of state
     * between different logging operations.
     *
     * @return A new instance of [LogBuilder], ready to be used for building log messages.
     */

    override fun provide(): LogBuilder = LogBuilder()
}
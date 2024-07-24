package com.lib.lokdroid.data.default_implementation

import com.lib.lokdroid.domain.LogBuilderProvider

/**
 * Provides a default implementation of the [LogBuilderProvider] interface by supplying instances of [DefaultLogBuilder].
 *
 * This class serves as a concrete provider for creating new [DefaultLogBuilder] instances, which are used
 * for constructing log messages in a structured format. It ensures that any logging system using this provider
 * gets a fresh instance of [DefaultLogBuilder] each time a log builder is requested.
 */

class DefaultLogBuilderProvider : LogBuilderProvider {

    /**
     * Creates and returns a new instance of [DefaultLogBuilder].
     *
     * This method is a straightforward factory for [DefaultLogBuilder], ensuring that each call
     * provides a new, isolated instance of the log builder, avoiding unintended sharing of state
     * between different logging operations.
     *
     * @return A new instance of [DefaultLogBuilder], ready to be used for building log messages.
     */

    override fun provide(): DefaultLogBuilder = DefaultLogBuilder()
}
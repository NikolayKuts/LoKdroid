package com.lib.lokdroid.data.default_implementation

/**
 * Provides the default log tag by deriving it from the current caller location.
 */
object TagProvider {

    /**
     * Resolves the default tag for the current log invocation.
     *
     * @return The caller file name without extension when available, or `????` when the caller cannot be determined.
     */
    fun getTag(): String {
        val callSite = getTargetReferenceCallSite() ?: return "????"

        return callSite.getTagName()
    }
}

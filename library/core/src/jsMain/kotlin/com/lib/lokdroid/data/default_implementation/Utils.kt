package com.lib.lokdroid.data.default_implementation

/**
 * Web builds currently do not provide a stable shared call-site parser, so the reference is omitted.
 */
internal actual fun getTargetReferenceCallSite(): CallSite? = null

/**
 * Returns a compact fallback reference for the JS target.
 */
internal actual fun getFormattedLineReference(): String = "????"

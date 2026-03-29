package com.lib.lokdroid.data.default_implementation

import com.lib.lokdroid.core.LoKdroid

/**
 * Resolves the desktop call site that should be treated as the originating log invocation.
 */
internal actual fun getTargetReferenceCallSite(): CallSite? {
    val stackTrace = Thread.currentThread().stackTrace
    val logInvocationIndex = stackTrace.indexOfLast {
        it.className.contains(LoKdroid::class.simpleName ?: "UnknownClass")
    }

    val stepToReferenceElement = 2
    if (logInvocationIndex == -1) return null

    return stackTrace.getOrNull(logInvocationIndex + stepToReferenceElement)?.toCallSite()
}

/**
 * Returns the desktop stack-frame-style reference used by [FormatterBuilder].
 */
internal actual fun getFormattedLineReference(): String = getClickableLineReference()

/**
 * Converts a JVM [StackTraceElement] into the normalized [CallSite] model used by shared code.
 */
private fun StackTraceElement.toCallSite(): CallSite = CallSite(
    className = className,
    methodName = methodName,
    fileName = fileName,
    lineNumber = lineNumber.takeIf { it >= 0 },
)

package com.lib.lokdroid.data.default_implementation

import com.lib.lokdroid.core.LoKdroid

internal fun getClickableLineReference(): String {
    val referenceElement = getTargetReferenceStackTraceElement() ?: return "\tat ???(Unknown Source)"

    val location = when {
        referenceElement.fileName != null && referenceElement.lineNumber >= 0 -> {
            "${referenceElement.fileName}:${referenceElement.lineNumber}"
        }

        referenceElement.fileName != null -> referenceElement.fileName
        referenceElement.isNativeMethod -> "Native Method"
        else -> "Unknown Source"
    }

    return "\t${referenceElement.getShortClassName()}.${referenceElement.methodName}($location)"
}

internal fun getTargetReferenceStackTraceElement(): StackTraceElement? {
    val stackTrace = Thread.currentThread().stackTrace
    val logInvocationIndex = stackTrace.indexOfLast {
        it.className.contains(LoKdroid::class.simpleName ?: "UnknownClass")
    }

    val stepToReferenceElement = 2
    return stackTrace.getOrNull(logInvocationIndex + stepToReferenceElement)
}

internal fun StackTraceElement.getShortClassName(): String {
    return className.substringAfterLast('.').substringBefore('$')
}

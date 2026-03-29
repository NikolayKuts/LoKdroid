package com.lib.lokdroid.data.default_implementation

/**
 * Normalized representation of a caller location resolved from a platform stack trace.
 *
 * @property className The fully qualified or synthetic owner name associated with the call site.
 * @property methodName The method or function name at the call site.
 * @property fileName The source file name when it is available.
 * @property lineNumber The source line number when it is available.
 */
internal data class CallSite(
    val className: String,
    val methodName: String,
    val fileName: String?,
    val lineNumber: Int?,
)

/**
 * Resolves the call site that should be treated as the user-level log invocation on the current platform.
 */
internal expect fun getTargetReferenceCallSite(): CallSite?

/**
 * Builds a stack-frame-like line reference suitable for desktop and iOS console output.
 *
 * @return A clickable reference when source metadata is available, or an `Unknown Source` fallback.
 */
internal fun getClickableLineReference(): String {
    val callSite = getTargetReferenceCallSite() ?: return "\tat ???(Unknown Source)"

    return "\t${callSite.getShortClassName()}.${callSite.getDisplayMethodName()}(${callSite.toLocation()})"
}

/**
 * Builds a compact `FileName:LineNumber` reference suitable for Android formatter output.
 *
 * @return The compact reference, or `????` when source metadata is unavailable.
 */
internal fun getLineReference(): String {
    val callSite = getTargetReferenceCallSite() ?: return "????"

    return callSite.toLocation(fallback = "????")
}

/**
 * Returns a stable display name for the call-site owner.
 *
 * When stack-trace metadata contains malformed or synthetic owner names, the source file name is used
 * to reconstruct a more useful synthetic Kotlin file-class name such as `SharedUiDemoAppKt`.
 */
internal fun CallSite.getShortClassName(): String {
    val ownerName = className.substringAfterLast('.')

    if (ownerName.startsWith("ComposableSingletons$")) {
        return ownerName
            .substringAfter('$')
            .substringBefore('$')
            .ifBlank { ownerName.substringBefore('$') }
    }

    val normalizedOwnerName = ownerName.substringBefore('$')

    if (normalizedOwnerName.isStableDisplayIdentifier()) {
        return normalizedOwnerName
    }

    return getFileClassNameFallback() ?: normalizedOwnerName.ifBlank { "UnknownClass" }
}

/**
 * Formats the source location portion of a call site.
 *
 * @param fallback The text to use when no file information is available.
 * @return Either `FileName:LineNumber`, `FileName`, or [fallback].
 */
private fun CallSite.toLocation(fallback: String = "Unknown Source"): String = when {
    fileName != null && lineNumber != null -> "$fileName:$lineNumber"
    fileName != null -> fileName
    else -> fallback
}

/**
 * Returns a stable display method name for line-reference rendering.
 *
 * Malformed native symbols are normalized to `invoke`, which is a better representation for Compose
 * lambdas than leaking raw signature fragments into console output.
 */
internal fun CallSite.getDisplayMethodName(): String {
    return if (methodName.isStableDisplayIdentifier()) methodName else "invoke"
}

private fun CallSite.getFileClassNameFallback(): String? {
    val baseName = fileName?.substringBeforeLast('.', missingDelimiterValue = fileName)
        ?.takeIf { !it.isNullOrBlank() }
        ?: return null

    return "${baseName}Kt"
}

private fun String.isStableDisplayIdentifier(): Boolean {
    if (isBlank()) return false

    return all { char ->
        char.isLetterOrDigit() || char == '_' || char == '$' || char == '<' || char == '>'
    }
}

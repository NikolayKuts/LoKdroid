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
 * Selects the first external call site after the internal logging entry point.
 *
 * @param stackTrace The platform stack frames.
 * @param logInvocationIndex The last frame index that still belongs to LoKdroid internals.
 * @param toCallSite Converts a platform frame into the normalized [CallSite] model.
 */
internal fun <T> resolveUserLogCallSite(
    stackTrace: List<T>,
    logInvocationIndex: Int,
    toCallSite: (T) -> CallSite,
): CallSite? {
    if (logInvocationIndex == -1) return null

    val candidateCallSite = stackTrace
        .drop(logInvocationIndex + 1)
        .asSequence()
        .map(toCallSite)
        .firstOrNull { callSite -> !callSite.isInternalLoggingWrapper() }

    return candidateCallSite
        ?: stackTrace.getOrNull(logInvocationIndex + 2)?.let(toCallSite)
}

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

internal fun CallSite.getTagName(): String {
    return getFileTagName() ?: getShortClassName()
}

internal fun CallSite.isInternalLoggingWrapper(): Boolean {
    return className in INTERNAL_LOGGING_OWNER_NAMES ||
        fileName in INTERNAL_LOGGING_CLASS_FILE_NAMES ||
        (
            (className == LOG_FUNCTIONS_CLASS_NAME || fileName == LOG_FUNCTIONS_FILE_NAME) &&
                methodName.startsWith(LOG_FUNCTIONS_METHOD_PREFIX)
            )
}

private fun CallSite.getFileClassNameFallback(): String? {
    val baseName = fileName?.substringBeforeLast('.', missingDelimiterValue = fileName)
        ?.takeIf { !it.isNullOrBlank() }
        ?: return null

    return "${baseName}Kt"
}

private fun CallSite.getFileTagName(): String? {
    return fileName
        ?.substringBeforeLast('.', missingDelimiterValue = fileName)
        ?.takeIf { it.isNotBlank() }
}

private fun String.isStableDisplayIdentifier(): Boolean {
    if (isBlank()) return false

    return all { char ->
        char.isLetterOrDigit() || char == '_' || char == '$' || char == '<' || char == '>'
    }
}

private val INTERNAL_LOGGING_OWNER_NAMES = setOf(
    LOKDROID_CLASS_NAME,
    LOG_MANAGER_CLASS_NAME,
)

private val INTERNAL_LOGGING_CLASS_FILE_NAMES = setOf(
    LOKDROID_FILE_NAME,
    LOG_MANAGER_FILE_NAME,
)

private const val LOKDROID_CLASS_NAME = "com.lib.lokdroid.core.LoKdroid"
private const val LOG_MANAGER_CLASS_NAME = "com.lib.lokdroid.core.LogManager"
private const val LOKDROID_FILE_NAME = "LoKdroid.kt"
private const val LOG_MANAGER_FILE_NAME = "LogManager.kt"
private const val LOG_FUNCTIONS_CLASS_NAME = "com.lib.lokdroid.core.LogFunctionsKt"
private const val LOG_FUNCTIONS_FILE_NAME = "LogFunctions.kt"
private const val LOG_FUNCTIONS_METHOD_PREFIX = "log"

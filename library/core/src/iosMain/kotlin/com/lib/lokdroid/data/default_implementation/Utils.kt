package com.lib.lokdroid.data.default_implementation

import com.lib.lokdroid.core.LoKdroid
import kotlin.experimental.ExperimentalNativeApi

/**
 * Resolves the iOS call site that should be treated as the originating log invocation.
 *
 * Kotlin/Native exposes stack traces as strings, so the selected frame is parsed and normalized.
 */
internal actual fun getTargetReferenceCallSite(): CallSite? {
    val stackTrace = getNativeStackTrace()
    val logInvocationIndex = stackTrace.indexOfLast {
        it.contains("kfun:${LoKdroid::class.qualifiedName}")
    }

    return resolveUserLogCallSite(stackTrace, logInvocationIndex) { it.toCallSite() }
}

/**
 * Returns the iOS stack-frame-style reference used by [FormatterBuilder].
 */
internal actual fun getFormattedLineReference(): String = getClickableLineReference()

/**
 * Captures the current Kotlin/Native stack trace as raw string frames.
 */
@OptIn(ExperimentalNativeApi::class)
private fun getNativeStackTrace(): List<String> {
    return Throwable().getStackTrace().toList()
}

/**
 * Parses a raw Kotlin/Native stack-trace frame into the normalized [CallSite] model.
 */
private fun String.toCallSite(): CallSite {
    val qualifiedSymbol = extractQualifiedSymbol()
    val (ownerName, methodName) = qualifiedSymbol.extractOwnerAndMethod()

    val locationMatch = LOCATION_REGEX.find(this)
    val rawPath = locationMatch?.groups?.get(1)?.value
    val fileName = rawPath?.substringAfterLast('/')
    val lineNumber = locationMatch?.groups?.get(2)?.value?.toIntOrNull()
    val className = ownerName.toNormalizedNativeClassName(fileName)

    return CallSite(
        className = className,
        methodName = methodName,
        fileName = fileName,
        lineNumber = lineNumber,
    )
}

private fun String.extractQualifiedSymbol(): String {
    return substringAfter("kfun:", missingDelimiterValue = "")
        .substringBefore(" +")
        .substringBefore(" (")
}

private fun String.extractOwnerAndMethod(): Pair<String, String> {
    val signatureIndex = indexOf('(')
    val ownerMethodSeparatorIndex = if (signatureIndex >= 0) {
        lastIndexOf('#', startIndex = signatureIndex)
    } else {
        -1
    }

    if (ownerMethodSeparatorIndex >= 0) {
        val ownerName = substring(0, ownerMethodSeparatorIndex)
        val methodName = substring(ownerMethodSeparatorIndex + 1, signatureIndex)
            .ifBlank { "unknownMethod" }

        return ownerName to methodName
    }

    val symbolWithoutSuffix = substringBefore('#')
    val ownerName = symbolWithoutSuffix.substringBeforeLast('.', missingDelimiterValue = symbolWithoutSuffix)
    val methodName = symbolWithoutSuffix.substringAfterLast('.', missingDelimiterValue = "unknownMethod")
        .ifBlank { "unknownMethod" }

    return ownerName to methodName
}

/**
 * Normalizes the owner name extracted from a native symbol.
 *
 * For top-level Kotlin functions, Kotlin/Native often exposes only the package path.
 * In that case the synthetic file-class name is reconstructed from [fileName].
 */
private fun String.toNormalizedNativeClassName(fileName: String?): String {
    if (isBlank()) return "UnknownClass"

    if (fileName != null && looksLikePackageName()) {
        val fileClassName = fileName.substringBeforeLast('.', missingDelimiterValue = fileName)
        return if (fileClassName.isBlank()) {
            this
        } else {
            "$this.${fileClassName}Kt"
        }
    }

    return this
}

/**
 * Returns `true` when the string looks like a package path rather than a class-like symbol owner.
 */
private fun String.looksLikePackageName(): Boolean {
    return split('.').all { segment ->
        segment.isNotBlank() && segment.first().isLowerCase()
    }
}

/** Regex used to extract `file:line[:column]` source metadata from native stack-trace frames. */
private val LOCATION_REGEX = Regex("""\((.+?):(\d+)(?::(\d+))?\)""")

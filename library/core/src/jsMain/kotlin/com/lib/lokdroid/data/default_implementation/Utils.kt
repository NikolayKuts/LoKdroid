package com.lib.lokdroid.data.default_implementation

/**
 * Resolves the browser call site that should be treated as the originating log invocation.
 *
 * Stack frames are mapped back to Kotlin sources through the source map of the script they belong to,
 * which turns bundled positions such as `webApp.js:26276:7` into `SharedUiScreens.kt:77`.
 */
internal actual fun getTargetReferenceCallSite(): CallSite? {
    return try {
        val stackFrames = captureBrowserStackTrace().toBrowserStackFrames()

        resolveUserLogCallSite(
            stackTrace = stackFrames,
            logInvocationIndex = stackFrames.indexOfLastLoKdroidFrame(),
            toCallSite = BrowserStackFrame::toCallSite,
        )
    } catch (_: Throwable) {
        null
    }
}

/**
 * Returns the browser stack-frame-style reference used by [FormatterBuilder].
 */
internal actual fun getFormattedLineReference(): String = getClickableLineReference()

/**
 * A single parsed frame of a browser stack trace.
 *
 * @property functionName The function name reported by the browser, when it reported one.
 * @property scriptUrl The URL of the script the frame points into.
 * @property lineNumber The one-based generated line number.
 * @property columnNumber The one-based generated column number.
 */
internal data class BrowserStackFrame(
    val functionName: String?,
    val scriptUrl: String,
    val lineNumber: Int,
    val columnNumber: Int,
)

/**
 * Parses the raw `Error.stack` text of any browser into the frames it describes.
 *
 * Lines that carry no source position, such as the leading `Error` line or frames pointing at native
 * code, are dropped.
 */
internal fun String.toBrowserStackFrames(): List<BrowserStackFrame> {
    return lineSequence()
        .mapNotNull(String::toBrowserStackFrameOrNull)
        .toList()
}

/**
 * Captures the current stack trace as reported by the JavaScript engine.
 *
 * V8 truncates stack traces to ten frames by default, which is shallower than the chain leading from a
 * log call through the formatter and back out to the caller, so the limit is raised for the duration of
 * the capture and restored afterwards to leave the host application's setting untouched.
 */
@Suppress("UnsafeCastFromDynamic")
private fun captureBrowserStackTrace(): String = js(
    """(function () {
        var previousLimit = Error.stackTraceLimit;
        if (typeof previousLimit === 'number' && previousLimit < 64) Error.stackTraceLimit = 64;
        var stack = new Error().stack || '';
        if (typeof previousLimit === 'number') Error.stackTraceLimit = previousLimit;
        return stack;
    })()"""
) as String

/**
 * Finds the last frame that still belongs to LoKdroid itself.
 *
 * Frames resolved through a source map are recognised by the Kotlin package their source file lives in.
 * The whole stack is searched rather than just its leading frames, because inlined standard library
 * calls such as `forEach` map back to their own stdlib sources and interrupt the run of LoKdroid frames.
 *
 * When no frame resolves — a bundler may inline modules into positions no reachable source map
 * describes — the search falls back to the script the stack was captured in, since that script is by
 * definition the one hosting LoKdroid's own code.
 *
 * @return The index of the last internal frame, or `-1` when no internal frame could be identified.
 */
private fun List<BrowserStackFrame>.indexOfLastLoKdroidFrame(): Int {
    val mappedIndex = indexOfLast { frame ->
        frame.resolveOriginalLocation()?.sourcePath?.isLoKdroidSourcePath() == true
    }

    if (mappedIndex != -1) return mappedIndex

    val captureScriptUrl = firstOrNull()?.scriptUrl ?: return -1

    return indexOfLast { frame -> frame.scriptUrl == captureScriptUrl }
}

/**
 * Reports whether a source map path points into one of LoKdroid's own Kotlin packages.
 */
internal fun String.isLoKdroidSourcePath(): Boolean {
    return LOKDROID_SOURCE_PATH_MARKERS.any { marker -> contains(marker) }
}

/**
 * Parses a single stack trace line emitted by a browser.
 *
 * @return The parsed frame, or `null` for lines that carry no source position, such as the leading
 * `Error` line or frames pointing at native code.
 */
internal fun String.toBrowserStackFrameOrNull(): BrowserStackFrame? {
    val match = CHROMIUM_STACK_FRAME_REGEX.matchEntire(trim())
        ?: FIREFOX_STACK_FRAME_REGEX.matchEntire(trim())
        ?: return null

    return BrowserStackFrame(
        functionName = match.groupValues[1].takeIf(String::isNotBlank),
        scriptUrl = match.groupValues[2].takeIf(String::isNotBlank) ?: return null,
        lineNumber = match.groupValues[3].toIntOrNull() ?: return null,
        columnNumber = match.groupValues[4].toIntOrNull() ?: return null,
    )
}

/**
 * Converts a browser frame into the normalized [CallSite] model used by shared code.
 *
 * When the frame resolves through a source map the call site describes the Kotlin source; otherwise it
 * falls back to the generated script position, which is still more useful than no reference at all.
 */
internal fun BrowserStackFrame.toCallSite(): CallSite {
    val originalLocation = resolveOriginalLocation()
    val methodName = functionName.toDisplayMethodName()

    if (originalLocation == null) {
        return CallSite(
            className = methodName,
            methodName = methodName,
            fileName = scriptUrl.substringBefore('?').substringAfterLast('/').takeIf(String::isNotBlank),
            lineNumber = lineNumber,
        )
    }

    return CallSite(
        className = originalLocation.toFileClassName(),
        methodName = methodName,
        fileName = originalLocation.fileName,
        lineNumber = originalLocation.lineNumber,
    )
}

/**
 * Maps this frame's generated position back to its Kotlin source position.
 */
private fun BrowserStackFrame.resolveOriginalLocation(): BrowserOriginalLocation? {
    return BrowserSourceMapResolver.resolve(
        location = BrowserGeneratedLocation(
            scriptUrl = scriptUrl,
            lineNumber = lineNumber,
            columnNumber = columnNumber,
        )
    )
}

/**
 * Builds the synthetic Kotlin file-class name for an original source location.
 *
 * The package is recovered from the source path so that shared rendering can shorten
 * `com.lib.lokdroid.demoapp.sharedui.SharedUiScreensKt` down to `SharedUiScreensKt`.
 */
internal fun BrowserOriginalLocation.toFileClassName(): String {
    val fileClassName = "${fileName.substringBeforeLast('.', missingDelimiterValue = fileName)}Kt"
    val packageName = sourcePath
        .substringBefore('?')
        .substringAfterLast(SOURCE_ROOT_DIRECTORY_MARKER, missingDelimiterValue = "")
        .substringBeforeLast('/', missingDelimiterValue = "")
        .replace('/', '.')

    return if (packageName.isBlank()) fileClassName else "$packageName.$fileClassName"
}

/**
 * Normalizes a browser function name into a readable Kotlin-style method name.
 *
 * Chromium reports receivers and aliases (`ClickableNode.eval [as onClick_1]`), and the Kotlin/JS
 * compiler mangles names to keep them unique (`log_1mx2b2_k$`, `logV_0`); both are stripped here.
 */
internal fun String?.toDisplayMethodName(): String {
    if (this == null) return DEFAULT_METHOD_NAME

    return (FUNCTION_NAME_ALIAS_REGEX.find(this)?.groupValues?.get(1) ?: this)
        .substringBefore(" (")
        .substringBefore('@')
        .substringAfterLast(' ')
        .substringAfterLast('.')
        .replace(MANGLED_METHOD_SUFFIX_REGEX, "")
        .takeIf(String::isNotBlank)
        ?: DEFAULT_METHOD_NAME
}

private const val DEFAULT_METHOD_NAME = "invoke"
private const val SOURCE_ROOT_DIRECTORY_MARKER = "/kotlin/"

/** Matches Chromium and Node frames: `at name (url:line:column)` or `at url:line:column`. */
private val CHROMIUM_STACK_FRAME_REGEX = Regex("""^at\s+(?:(.+?)\s+\()?(.+?):(\d+):(\d+)\)?$""")

/** Matches Firefox and Safari frames: `name@url:line:column`. */
private val FIREFOX_STACK_FRAME_REGEX = Regex("""^(.*?)@(.+?):(\d+):(\d+)$""")

/** Matches the alias Chromium appends when a function is called through another binding. */
private val FUNCTION_NAME_ALIAS_REGEX = Regex("""\[as\s+([^\]]+)\]""")

/** Matches the uniqueness suffixes appended by the Kotlin/JS compiler, such as `_1mx2b2_k$` or `_0`. */
private val MANGLED_METHOD_SUFFIX_REGEX = Regex("""(?:_[0-9a-z]+_k\$|_\d+)$""")

/** Kotlin package directories that belong to LoKdroid itself rather than to the code using it. */
private val LOKDROID_SOURCE_PATH_MARKERS = listOf(
    "/com/lib/lokdroid/core/",
    "/com/lib/lokdroid/data/",
    "/com/lib/lokdroid/domain/",
)

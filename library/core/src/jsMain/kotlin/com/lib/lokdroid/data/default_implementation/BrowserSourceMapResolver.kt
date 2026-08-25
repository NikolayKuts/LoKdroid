package com.lib.lokdroid.data.default_implementation

import org.w3c.dom.url.URL
import org.w3c.xhr.XMLHttpRequest

/**
 * A position inside a bundled script, exactly as reported by a browser stack frame.
 *
 * @property scriptUrl The absolute URL of the script the frame belongs to.
 * @property lineNumber The one-based generated line number.
 * @property columnNumber The one-based generated column number.
 */
internal data class BrowserGeneratedLocation(
    val scriptUrl: String,
    val lineNumber: Int,
    val columnNumber: Int,
)

/**
 * A position inside an original source file recovered from a source map.
 *
 * @property sourcePath The source path declared by the source map.
 * @property fileName The file name extracted from [sourcePath].
 * @property lineNumber The one-based original line number.
 */
internal data class BrowserOriginalLocation(
    val sourcePath: String,
    val fileName: String,
    val lineNumber: Int,
)

/**
 * Maps browser stack-frame positions back to the Kotlin sources they were compiled from.
 *
 * Only the source map that a script declares for itself is used, so generated positions and mappings
 * always share the same coordinate space. Bundlers that inline modules through `eval` (webpack's
 * default `eval-source-map` devtool, for example) report positions in a coordinate space that no
 * fetchable source map describes; such frames are left unresolved instead of being mapped to a
 * plausible but wrong Kotlin line.
 *
 * Source maps are downloaded with a blocking request because [getTargetReferenceCallSite] has to answer
 * synchronously. Each script is downloaded at most once and the parsed index is reused afterwards.
 */
internal object BrowserSourceMapResolver {

    /**
     * Parsed source map indices, keyed by script URL. A `null` value records a script that has no
     * usable map, so the failed download is never retried; the key's presence, not the value, marks
     * a script as already looked at.
     */
    private val indicesByScriptUrl = mutableMapOf<String, SourceMapIndex?>()

    /**
     * Resolves the original Kotlin position for a generated [location].
     *
     * @return The original position, or `null` when the script has no reachable source map or the
     * position is not covered by it.
     */
    fun resolve(location: BrowserGeneratedLocation): BrowserOriginalLocation? {
        val scriptUrl = location.scriptUrl
        if (scriptUrl !in indicesByScriptUrl) {
            indicesByScriptUrl[scriptUrl] = loadIndex(scriptUrl)
        }

        return indicesByScriptUrl[scriptUrl]?.findOriginalLocation(
            generatedLineNumber = location.lineNumber,
            generatedColumnNumber = location.columnNumber,
        )
    }

    /**
     * Downloads and parses the source map belonging to [scriptUrl].
     *
     * The conventional `<script>.map` sibling is tried first, because every mainstream bundler writes
     * it there. Only when that is missing is the `sourceMappingURL` comment read from the tail of the
     * script itself, and just from a range request, so that an oversized bundle is never downloaded in
     * full only to read its last line.
     */
    private fun loadIndex(scriptUrl: String): SourceMapIndex? {
        if (!scriptUrl.startsWith("http://") && !scriptUrl.startsWith("https://")) return null

        val siblingSourceMapUrl = "$scriptUrl.map"
        val sourceMapText = fetchText(siblingSourceMapUrl)
            ?: findDeclaredSourceMapUrl(scriptUrl)
                ?.takeIf { declaredUrl -> declaredUrl != siblingSourceMapUrl }
                ?.let { declaredUrl -> fetchText(declaredUrl) }
            ?: return null

        return parseSourceMapIndex(sourceMapText)
    }

    /**
     * Reads the `sourceMappingURL` comment from the tail of a script.
     *
     * @return The absolute source map URL, or `null` when the script declares none, declares an inline
     * one, or the server does not support range requests.
     */
    private fun findDeclaredSourceMapUrl(scriptUrl: String): String? {
        val scriptTail = fetchText(url = scriptUrl, rangeSuffixBytes = SOURCE_MAPPING_URL_LOOKUP_BYTES)
            ?: return null
        val declaredSourceMapUrl = SOURCE_MAPPING_URL_REGEX.findAll(scriptTail)
            .lastOrNull()
            ?.groupValues
            ?.get(1)
            ?.takeUnless { it.startsWith("data:") }
            ?: return null

        return resolveAgainst(url = declaredSourceMapUrl, baseUrl = scriptUrl)
    }
}

/**
 * Performs a blocking `XMLHttpRequest` and returns the response body.
 *
 * @param url The resource to download.
 * @param rangeSuffixBytes When set, requests only the trailing bytes and gives up unless the server
 * answers with a partial response.
 * @return The response body, or `null` when the request failed or was answered unusably.
 */
private fun fetchText(url: String, rangeSuffixBytes: Int? = null): String? {
    return try {
        val request = XMLHttpRequest()
        request.open(method = "GET", url = url, async = false)
        if (rangeSuffixBytes != null) request.setRequestHeader("Range", "bytes=-$rangeSuffixBytes")
        request.send()

        when {
            request.status.toInt() !in 200..299 -> null
            rangeSuffixBytes != null && request.status.toInt() != HTTP_STATUS_PARTIAL_CONTENT -> null
            else -> request.responseText
        }
    } catch (_: Throwable) {
        null
    }
}

/**
 * Resolves a possibly relative [url] against [baseUrl].
 */
private fun resolveAgainst(url: String, baseUrl: String): String? {
    return try {
        URL(url = url, base = baseUrl).href
    } catch (_: Throwable) {
        null
    }
}

/**
 * A source map prepared for repeated position lookups.
 *
 * Segment data stays in its encoded [mappings] form; only the decoder state at the start of every
 * generated line is materialised, so a lookup decodes a single line instead of the whole map.
 *
 * @property sources The original source paths declared by the map.
 * @property mappings The raw Base64 VLQ mappings.
 * @property lineOffsets For every generated line, the index in [mappings] where its segments start.
 * @property sourceIndexAtLineStart The running source index at the start of every generated line.
 * @property originalLineAtLineStart The running original line at the start of every generated line.
 */
internal class SourceMapIndex(
    private val sources: List<String>,
    private val mappings: String,
    private val lineOffsets: IntArray,
    private val sourceIndexAtLineStart: IntArray,
    private val originalLineAtLineStart: IntArray,
) {

    /**
     * Finds the original position covering a generated position.
     *
     * @return The closest mapping at or before the generated column, or `null` when the line carries
     * no mapping with original position data.
     */
    fun findOriginalLocation(
        generatedLineNumber: Int,
        generatedColumnNumber: Int,
    ): BrowserOriginalLocation? {
        val lineIndex = generatedLineNumber - 1
        if (lineIndex !in lineOffsets.indices) return null

        val targetGeneratedColumn = (generatedColumnNumber - 1).coerceAtLeast(0)
        val cursor = MappingsCursor(mappings, lineOffsets[lineIndex])
        var sourceIndex = sourceIndexAtLineStart[lineIndex]
        var originalLine = originalLineAtLineStart[lineIndex]
        var generatedColumn = 0
        var bestSourceIndex = -1
        var bestOriginalLine = -1

        while (cursor.moveToNextSegment()) {
            val fieldCount = cursor.readSegment()
            if (fieldCount == 0) break

            generatedColumn += cursor.field(SEGMENT_FIELD_GENERATED_COLUMN)
            if (fieldCount < SEGMENT_FIELDS_WITH_ORIGINAL_POSITION) continue

            sourceIndex += cursor.field(SEGMENT_FIELD_SOURCE_INDEX)
            originalLine += cursor.field(SEGMENT_FIELD_ORIGINAL_LINE)

            if (generatedColumn <= targetGeneratedColumn || bestSourceIndex == -1) {
                bestSourceIndex = sourceIndex
                bestOriginalLine = originalLine
            }
        }

        val sourcePath = sources.getOrNull(bestSourceIndex) ?: return null

        return BrowserOriginalLocation(
            sourcePath = sourcePath,
            fileName = sourcePath.substringBefore('?').substringAfterLast('/'),
            lineNumber = bestOriginalLine + 1,
        )
    }
}

/**
 * Parses a source map document into a [SourceMapIndex].
 */
@Suppress("UnsafeCastFromDynamic")
internal fun parseSourceMapIndex(sourceMapText: String): SourceMapIndex? {
    return try {
        val sourceMap: dynamic = JSON.parse<dynamic>(sourceMapText)
        val mappings = sourceMap.mappings as? String ?: return null
        val sourceRoot = (sourceMap.sourceRoot as? String)?.takeIf(String::isNotBlank)
        val sources = (sourceMap.sources as? Array<String> ?: return null)
            .map { source -> source.prefixedWith(sourceRoot) }

        buildSourceMapIndex(sources = sources, mappings = mappings)
    } catch (_: Throwable) {
        null
    }
}

/**
 * Prepends [sourceRoot] to a source map entry unless the entry is already absolute.
 */
private fun String.prefixedWith(sourceRoot: String?): String {
    if (sourceRoot == null) return this
    if (startsWith('/') || contains("://")) return this

    return "${sourceRoot.trimEnd('/')}/${removePrefix("./")}"
}

/**
 * Walks the encoded mappings once and records the decoder state at the start of every generated line.
 *
 * Bundle source maps reach millions of segments, so the walk decodes in place and only keeps the two
 * running counters a later lookup cannot recompute on its own.
 */
private fun buildSourceMapIndex(sources: List<String>, mappings: String): SourceMapIndex {
    val lineCount = mappings.count { it == MAPPINGS_LINE_SEPARATOR } + 1
    val lineOffsets = IntArray(lineCount)
    val sourceIndexAtLineStart = IntArray(lineCount)
    val originalLineAtLineStart = IntArray(lineCount)

    val cursor = MappingsCursor(mappings)
    var sourceIndex = 0
    var originalLine = 0

    for (lineIndex in 0 until lineCount) {
        lineOffsets[lineIndex] = cursor.offset
        sourceIndexAtLineStart[lineIndex] = sourceIndex
        originalLineAtLineStart[lineIndex] = originalLine

        while (cursor.moveToNextSegment()) {
            val fieldCount = cursor.readSegment()
            if (fieldCount == 0) break
            if (fieldCount < SEGMENT_FIELDS_WITH_ORIGINAL_POSITION) continue

            sourceIndex += cursor.field(SEGMENT_FIELD_SOURCE_INDEX)
            originalLine += cursor.field(SEGMENT_FIELD_ORIGINAL_LINE)
        }

        cursor.moveToNextLine()
    }

    return SourceMapIndex(
        sources = sources,
        mappings = mappings,
        lineOffsets = lineOffsets,
        sourceIndexAtLineStart = sourceIndexAtLineStart,
        originalLineAtLineStart = originalLineAtLineStart,
    )
}

/**
 * A forward-only cursor over the Base64 VLQ encoded `mappings` field of a source map.
 *
 * Decoded fields are written into a reusable buffer instead of a fresh list per segment, which keeps a
 * full walk over a multi-megabyte bundle map free of per-segment allocations.
 *
 * @property offset The current position in [mappings].
 */
private class MappingsCursor(private val mappings: String, var offset: Int = 0) {

    private val fields = IntArray(SEGMENT_FIELD_COUNT)

    /**
     * Advances to the next segment of the current generated line.
     *
     * @return `true` when a segment is available, `false` at the end of the line or of the mappings.
     */
    fun moveToNextSegment(): Boolean {
        while (offset < mappings.length && mappings[offset] == MAPPINGS_SEGMENT_SEPARATOR) offset++

        return offset < mappings.length && mappings[offset] != MAPPINGS_LINE_SEPARATOR
    }

    /**
     * Decodes the segment at the cursor into the field buffer.
     *
     * @return The number of decoded fields, which is `0` for a malformed segment.
     */
    fun readSegment(): Int {
        var fieldCount = 0

        while (offset < mappings.length) {
            val char = mappings[offset]
            if (char == MAPPINGS_SEGMENT_SEPARATOR || char == MAPPINGS_LINE_SEPARATOR) break

            var accumulator = 0
            var shift = 0
            var hasContinuationBit: Boolean

            do {
                val digit = mappings[offset].toBase64DigitOrNull()
                if (digit == null) {
                    offset++
                    return fieldCount
                }
                offset++

                hasContinuationBit = (digit and VLQ_CONTINUATION_BIT) != 0
                accumulator += (digit and VLQ_VALUE_MASK) shl shift
                shift += VLQ_SHIFT
            } while (hasContinuationBit && offset < mappings.length)

            val magnitude = accumulator shr 1
            if (fieldCount < fields.size) {
                fields[fieldCount] = if (accumulator and 1 == 1) -magnitude else magnitude
            }
            fieldCount++
        }

        return fieldCount
    }

    /**
     * Returns a field decoded by the most recent [readSegment] call.
     */
    fun field(index: Int): Int = fields[index]

    /**
     * Advances the cursor past the end of the current generated line.
     */
    fun moveToNextLine() {
        while (offset < mappings.length && mappings[offset] != MAPPINGS_LINE_SEPARATOR) offset++
        if (offset < mappings.length) offset++
    }
}

/**
 * Returns the Base64 alphabet index of this character, or `null` when it is not a Base64 digit.
 */
private fun Char.toBase64DigitOrNull(): Int? {
    val charCode = code
    if (charCode >= BASE64_DIGIT_BY_CHAR_CODE.size) return null

    return BASE64_DIGIT_BY_CHAR_CODE[charCode].takeIf { it >= 0 }
}

private const val MAPPINGS_LINE_SEPARATOR = ';'
private const val MAPPINGS_SEGMENT_SEPARATOR = ','

/** A mapping segment holds up to five fields; the fifth, an index into `names`, is not used here. */
private const val SEGMENT_FIELD_COUNT = 5
private const val SEGMENT_FIELD_GENERATED_COLUMN = 0
private const val SEGMENT_FIELD_SOURCE_INDEX = 1
private const val SEGMENT_FIELD_ORIGINAL_LINE = 2
private const val SEGMENT_FIELDS_WITH_ORIGINAL_POSITION = 4
private const val SOURCE_MAPPING_URL_LOOKUP_BYTES = 4096
private const val HTTP_STATUS_PARTIAL_CONTENT = 206
private const val VLQ_SHIFT = 5
private const val VLQ_CONTINUATION_BIT = 1 shl VLQ_SHIFT
private const val VLQ_VALUE_MASK = VLQ_CONTINUATION_BIT - 1

private val SOURCE_MAPPING_URL_REGEX = Regex("""[#@]\s*sourceMappingURL\s*=\s*(\S+)""")

private val BASE64_DIGIT_BY_CHAR_CODE = IntArray(128) { -1 }.apply {
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
        .forEachIndexed { digit, char -> this[char.code] = digit }
}

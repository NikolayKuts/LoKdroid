package com.lib.lokdroid.data.default_implementation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Covers Base64 VLQ decoding and generated-to-original position lookup.
 *
 * The fixture map describes three generated lines:
 * - line 1 maps column 0 to `Screen.kt:1` and column 10 to `Screen.kt:5`
 * - line 2 carries no mapping at all
 * - line 3 maps column 0 to `Other.kt:10`, which also exercises the running counters carried across
 *   lines, since every field of a segment is stored as a delta from the previous one.
 */
class BrowserSourceMapIndexTest {

    private val sourceMap = """
        {
          "version": 3,
          "sources": [
            "src/commonMain/kotlin/com/example/demo/Screen.kt",
            "src/commonMain/kotlin/com/example/demo/Other.kt"
          ],
          "mappings": "AAAA,UAIE;;ACKF"
        }
    """.trimIndent()

    private val index: SourceMapIndex
        get() = assertNotNull(parseSourceMapIndex(sourceMap), "fixture source map should parse")

    @Test
    fun `resolves the first mapping of a generated line`() {
        val location = assertNotNull(index.findOriginalLocation(generatedLineNumber = 1, generatedColumnNumber = 1))

        assertEquals("Screen.kt", location.fileName)
        assertEquals(1, location.lineNumber)
        assertEquals("src/commonMain/kotlin/com/example/demo/Screen.kt", location.sourcePath)
    }

    @Test
    fun `resolves the mapping that starts exactly at the requested column`() {
        val location = assertNotNull(index.findOriginalLocation(generatedLineNumber = 1, generatedColumnNumber = 11))

        assertEquals("Screen.kt", location.fileName)
        assertEquals(5, location.lineNumber)
    }

    @Test
    fun `picks the closest mapping at or before the requested column`() {
        val location = assertNotNull(index.findOriginalLocation(generatedLineNumber = 1, generatedColumnNumber = 5))

        assertEquals(1, location.lineNumber)
    }

    @Test
    fun `carries decoder state across an unmapped generated line`() {
        val location = assertNotNull(index.findOriginalLocation(generatedLineNumber = 3, generatedColumnNumber = 1))

        assertEquals("Other.kt", location.fileName)
        assertEquals(10, location.lineNumber)
    }

    @Test
    fun `returns null for a generated line without mappings`() {
        assertNull(index.findOriginalLocation(generatedLineNumber = 2, generatedColumnNumber = 1))
    }

    @Test
    fun `returns null for a generated line outside the map`() {
        assertNull(index.findOriginalLocation(generatedLineNumber = 99, generatedColumnNumber = 1))
    }

    @Test
    fun `prepends the source root to relative sources`() {
        val rootedMap = """
            {
              "version": 3,
              "sourceRoot": "webpack://app/",
              "sources": ["./src/commonMain/kotlin/com/example/demo/Screen.kt"],
              "mappings": "AAAA"
            }
        """.trimIndent()
        val rootedIndex = assertNotNull(parseSourceMapIndex(rootedMap))

        val location = assertNotNull(rootedIndex.findOriginalLocation(generatedLineNumber = 1, generatedColumnNumber = 1))

        assertEquals("webpack://app/src/commonMain/kotlin/com/example/demo/Screen.kt", location.sourcePath)
    }

    @Test
    fun `decodes multi character vlq values`() {
        // "yC" encodes a generated column delta of 41, which needs two VLQ digits, and the segment maps
        // that column on to the next original line.
        val wideMap = """
            {
              "version": 3,
              "sources": ["Wide.kt"],
              "mappings": "AAAA,yCACA"
            }
        """.trimIndent()
        val wideIndex = assertNotNull(parseSourceMapIndex(wideMap))

        assertEquals(1, assertNotNull(wideIndex.findOriginalLocation(1, 41)).lineNumber)
        assertEquals(2, assertNotNull(wideIndex.findOriginalLocation(1, 42)).lineNumber)
    }

    @Test
    fun `returns null for a document that is not a source map`() {
        assertNull(parseSourceMapIndex("not json at all"))
        assertNull(parseSourceMapIndex("""{"version":3}"""))
    }
}

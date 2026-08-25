package com.lib.lokdroid.data.default_implementation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Covers the browser-specific half of caller resolution: turning a raw `Error.stack` into frames,
 * telling LoKdroid's own sources apart from the code using it, and normalizing the names browsers and
 * the Kotlin/JS compiler produce.
 *
 * The stack samples are verbatim captures from Chromium running the web demo app.
 */
class BrowserCallSiteResolutionTest {

    @Test
    fun `parses a chromium stack into frames`() {
        val stack = """
            Error
                at protoOf.log_1mx2b2_k${'$'} (http://localhost:8099/webApp.js:24495:30)
                at logV_0 (http://localhost:8099/webApp.js:22595:28)
                at ClickableNode.onClick_1 (http://localhost:8099/webApp.js:26276:7)
                at http://localhost:8099/webApp.js:88770:16
        """.trimIndent()

        val frames = stack.toBrowserStackFrames()

        assertEquals(4, frames.size)
        assertEquals(
            BrowserStackFrame(
                functionName = "ClickableNode.onClick_1",
                scriptUrl = "http://localhost:8099/webApp.js",
                lineNumber = 26276,
                columnNumber = 7,
            ),
            frames[2],
        )
        assertNull(frames[3].functionName)
        assertEquals(88770, frames[3].lineNumber)
    }

    @Test
    fun `parses a firefox stack into frames`() {
        val stack = """
            logV_0@http://localhost:8099/webApp.js:22595:28
            @http://localhost:8099/webApp.js:88770:16
        """.trimIndent()

        val frames = stack.toBrowserStackFrames()

        assertEquals(2, frames.size)
        assertEquals("logV_0", frames[0].functionName)
        assertEquals("http://localhost:8099/webApp.js", frames[0].scriptUrl)
        assertEquals(22595, frames[0].lineNumber)
        assertEquals(28, frames[0].columnNumber)
        assertNull(frames[1].functionName)
    }

    @Test
    fun `drops stack lines without a source position`() {
        val stack = """
            Error
                at <anonymous>
                at logV_0 (http://localhost:8099/webApp.js:22595:28)
        """.trimIndent()

        assertEquals(1, stack.toBrowserStackFrames().size)
    }

    @Test
    fun `recognizes lokdroid sources and leaves consumer sources alone`() {
        assertTrue("webpack://app/../library/core/src/commonMain/kotlin/com/lib/lokdroid/core/LogFunctions.kt".isLoKdroidSourcePath())
        assertTrue("webpack://app/../library/core/src/jsMain/kotlin/com/lib/lokdroid/data/default_implementation/Utils.kt".isLoKdroidSourcePath())
        assertTrue("webpack://app/../library/domain/src/commonMain/kotlin/com/lib/lokdroid/domain/ILogger.kt".isLoKdroidSourcePath())

        assertEquals(
            false,
            "webpack://app/../demoApp/sharedUI/src/commonMain/kotlin/com/lib/lokdroid/demoapp/sharedui/SharedUiScreens.kt"
                .isLoKdroidSourcePath(),
        )
        assertEquals(
            false,
            "webpack://app/../compose/foundation/src/commonMain/kotlin/androidx/compose/foundation/Clickable.kt"
                .isLoKdroidSourcePath(),
        )
    }

    @Test
    fun `strips kotlin js name mangling from method names`() {
        assertEquals("log", "protoOf.log_1mx2b2_k${'$'}".toDisplayMethodName())
        assertEquals("logV", "logV_0".toDisplayMethodName())
        assertEquals("onClick", "ClickableNode.onClick_1".toDisplayMethodName())
        assertEquals("main", "Object.main".toDisplayMethodName())
        assertEquals("DemoScreen", "new DemoScreen".toDisplayMethodName())
    }

    @Test
    fun `prefers the chromium alias over the binding it was called through`() {
        assertEquals("onClick", "ClickableNode.eval [as onClick_1]".toDisplayMethodName())
    }

    @Test
    fun `falls back to invoke for an unnamed frame`() {
        assertEquals("invoke", null.toDisplayMethodName())
    }

    @Test
    fun `builds a synthetic file class name from the source package`() {
        val location = BrowserOriginalLocation(
            sourcePath = "webpack://app/../demoApp/sharedUI/src/commonMain/kotlin/com/lib/lokdroid/demoapp/sharedui/SharedUiScreens.kt",
            fileName = "SharedUiScreens.kt",
            lineNumber = 77,
        )

        assertEquals("com.lib.lokdroid.demoapp.sharedui.SharedUiScreensKt", location.toFileClassName())
        assertEquals("SharedUiScreensKt", location.toFileClassName().substringAfterLast('.'))
    }

    @Test
    fun `builds a file class name when the source carries no package directories`() {
        val location = BrowserOriginalLocation(
            sourcePath = "webpack://app/../build/compileSync/js/main/developmentExecutable/kotlin/LocalTimeFormat.kt",
            fileName = "LocalTimeFormat.kt",
            lineNumber = 12,
        )

        assertEquals("LocalTimeFormatKt", location.toFileClassName())
    }

    @Test
    fun `falls back to the generated position when no source map can be reached`() {
        val frame = BrowserStackFrame(
            functionName = "ClickableNode.eval [as onClick_1]",
            scriptUrl = "webpack-internal:///./kotlin/LoKdroid-sharedUI.js",
            lineNumber = 1568,
            columnNumber = 7,
        )

        val callSite = frame.toCallSite()

        assertEquals("onClick", callSite.methodName)
        assertEquals("LoKdroid-sharedUI.js", callSite.fileName)
        assertEquals(1568, callSite.lineNumber)
    }
}

package com.lib.lokdroid.data.default_implementation

import kotlin.test.Test
import kotlin.test.assertEquals

class CallSiteTest {

    @Test
    fun `compose singleton owner resolves to file synthetic class name`() {
        val callSite = CallSite(
            className = "com.lib.lokdroid.demoapp.desktop.ComposableSingletons\$DesktopMainKt",
            methodName = "invoke",
            fileName = "DesktopMain.kt",
            lineNumber = 24,
        )

        assertEquals("DesktopMainKt", callSite.getShortClassName())
    }

    @Test
    fun `malformed native symbol falls back to file class and invoke`() {
        val callSite = CallSite(
            className = "Int;kotlin",
            methodName = "Int){}",
            fileName = "SharedUiDemoApp.kt",
            lineNumber = 35,
        )

        assertEquals("SharedUiDemoAppKt", callSite.getShortClassName())
        assertEquals("invoke", callSite.getDisplayMethodName())
    }
}

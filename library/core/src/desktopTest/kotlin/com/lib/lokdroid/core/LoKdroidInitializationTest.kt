package com.lib.lokdroid.core

import kotlin.test.Test
import kotlin.test.assertEquals

class LoKdroidInitializationTest {

    @Test
    fun `logging works before explicit initialize and explicit initialize still overrides logger`() {
        logI(message = "default logger should be available")

        var capturedMessage: String? = null

        LoKdroid.initialize(
            logger = { _, _, message -> capturedMessage = message },
        )

        logI(message = "custom logger should receive this")

        assertEquals("custom logger should receive this", capturedMessage)
    }
}

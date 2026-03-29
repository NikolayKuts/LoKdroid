package com.lib.lokdroid.data.default_implementation

import com.lib.lokdroid.core.LoKdroid
import com.lib.lokdroid.core.logI
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FormatterBuilderTest {

    @Test
    fun `withLineReference appends a desktop stack frame without breaking android style builder usage`() {
        var loggedMessage: String? = null

        LoKdroid.initialize(
            logger = { _, _, message -> loggedMessage = message },
            formatter = FormatterBuilder()
                .withPointer()
                .space()
                .withLineReference()
                .space()
                .message()
                .space()
                .custom(text = "some custom text")
                .build()
        )

        logI(message = "some message")

        val message = assertNotNull(loggedMessage)
        assertTrue(message.startsWith("---> \tFormatterBuilderTest."))
        assertTrue(message.contains("(FormatterBuilderTest.kt:"))
        assertTrue(message.contains(" some message some custom text"))
    }
}

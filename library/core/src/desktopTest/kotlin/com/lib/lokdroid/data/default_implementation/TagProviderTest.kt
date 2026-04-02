package com.lib.lokdroid.data.default_implementation

import com.lib.lokdroid.core.LoKdroid
import com.lib.lokdroid.core.logI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TagProviderTest {

    @Test
    fun `desktop default tag matches caller file name`() {
        var capturedTag: String? = null

        LoKdroid.initialize(
            logger = { _, tag, _ -> capturedTag = tag },
        )

        logI(message = "tag check")

        assertEquals("TagProviderTest", assertNotNull(capturedTag))
    }
}

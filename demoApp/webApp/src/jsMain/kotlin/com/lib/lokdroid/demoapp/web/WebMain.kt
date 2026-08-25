package com.lib.lokdroid.demoapp.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.lib.lokdroid.core.LoKdroid
import com.lib.lokdroid.core.log
import com.lib.lokdroid.data.default_implementation.FormatterBuilder
import com.lib.lokdroid.demoapp.sharedui.SharedUiDemoApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    LoKdroid.initialize(
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

    log {
        "init"()
        "Web"(I)
    }

    CanvasBasedWindow(title = "LoKdroid Web Demo") {
        SharedUiDemoApp()
    }
}

package com.lib.lokdroid.demoapp.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lib.lokdroid.core.LoKdroid
import com.lib.lokdroid.core.log
import com.lib.lokdroid.data.default_implementation.FormatterBuilder
import com.lib.lokdroid.demoapp.sharedui.SharedUiDemoApp

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
        "Desktop"(I)
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "LoKdroid Desktop Demo",
        ) {
            SharedUiDemoApp()
        }
    }
}

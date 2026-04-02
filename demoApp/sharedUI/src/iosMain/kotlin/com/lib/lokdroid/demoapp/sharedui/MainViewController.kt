package com.lib.lokdroid.demoapp.sharedui

import androidx.compose.ui.window.ComposeUIViewController
import com.lib.lokdroid.core.LoKdroid
import com.lib.lokdroid.core.log
import com.lib.lokdroid.data.default_implementation.FormatterBuilder
import platform.UIKit.UIViewController

fun initializeLoKdroid() {
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
}

fun MainViewController(): UIViewController = ComposeUIViewController {
    SharedUiDemoApp()
}

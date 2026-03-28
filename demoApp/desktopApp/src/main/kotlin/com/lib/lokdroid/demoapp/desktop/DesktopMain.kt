package com.lib.lokdroid.demoapp.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lib.lokdroid.core.LoKdroid
import com.lib.lokdroid.demoapp.sharedui.SharedUiDemoApp

fun main() {
    LoKdroid.initialize()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "LoKdroid Desktop Demo",
        ) {
            SharedUiDemoApp()
        }
    }
}

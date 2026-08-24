package com.lib.lokdroid.demoapp.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.lib.lokdroid.demoapp.sharedui.SharedUiDemoApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(title = "LoKdroid Web Demo") {
        SharedUiDemoApp()
    }
}

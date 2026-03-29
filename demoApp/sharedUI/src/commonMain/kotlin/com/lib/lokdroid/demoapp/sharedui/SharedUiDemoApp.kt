package com.lib.lokdroid.demoapp.sharedui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.lib.lokdroid.core.logV

private val DemoData = listOf(
    "first",
    "second",
    "third",
    "forth",
    "fifth",
    "sixth",
)

internal enum class DemoScreen {
    SingleLogging,
    MultipleLogging,
}

@Composable
fun SharedUiDemoApp(
    modifier: Modifier = Modifier,
    data: List<String> = DemoData,
) {
    val items = data.ifEmpty { listOf("No demo data") }

    logV {
        "multi log"()
        "some Error"(E)
        "some Info"(I)
        "some Debug"(D)
        "some Verbose"(V)
        "some Warn"(W)
    }

    SharedUiTheme {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            var activeScreen by remember { mutableStateOf(DemoScreen.SingleLogging) }

            when (activeScreen) {
                DemoScreen.SingleLogging -> SingleLoggingScreen(
                    data = items,
                    onShowMultipleLogging = { activeScreen = DemoScreen.MultipleLogging },
                )

                DemoScreen.MultipleLogging -> MultipleLoggingScreen(
                    data = items,
                    onShowSingleLogging = { activeScreen = DemoScreen.SingleLogging },
                )
            }
        }
    }
}

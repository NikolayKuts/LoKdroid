package com.lib.lokdroid

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lib.lokdroid.core.log
import com.lib.lokdroid.domain.model.Level

@Composable
fun MultipleLoggingScreen(data: List<String>) {
    var selectedItemIndex by remember {
        mutableIntStateOf(value = 0)
    }

    val selectedItemValue = data[selectedItemIndex]

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Items(
            data = data,
            selectedItemIndex = selectedItemIndex,
            onItemClick = { index -> selectedItemIndex = index }
        )

        Column {
            ButtonsRow(
                selectedItemValue = selectedItemValue,
                forVerbose = "Multi V" to { invokeMultipleLog(level = Level.Verbose, data = data) },
                forDebug = "Multi D" to { invokeMultipleLog(level = Level.Debug, data = data) },
                forInfo = "Multi I" to { invokeMultipleLog(level = Level.Info, data = data) },
                forWarn = "Multi W" to { invokeMultipleLog(level = Level.Warn, data = data) },
                forError = "Multi E" to { invokeMultipleLog(level = Level.Error, data = data) },
            )
        }
    }
}

private fun invokeMultipleLog(level: Level, data: List<String>) {
    log(level = level) {
        message("Multiple log")
        data.forEach { message(value = it) }
    }
}
package com.lib.lokdroid

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lib.lokdroid.core.logD
import com.lib.lokdroid.core.logE
import com.lib.lokdroid.core.logI
import com.lib.lokdroid.core.logV
import com.lib.lokdroid.core.logW

@Composable
fun SingleLoggingScreen(
    data: List<String>,
    toMultipleLoggingScreen: () -> Unit,
) {
    var selectedItemIndex by remember { mutableIntStateOf(value = 0) }
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
                forVerbose = "Verbose" to { logV(message = it) },
                forDebug = "Debug" to { logD(message = it) },
                forInfo = "Info" to { logI(message = it) },
                forWarn = "Warn" to { logW(message = it) },
                forError = "Error" to { logE(message = it) },
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                onClick = toMultipleLoggingScreen
            ) {
                Text(text = "To multiple logging")
            }
        }
    }
}
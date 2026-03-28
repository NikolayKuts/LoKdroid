package com.lib.lokdroid.demoapp.sharedui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lib.lokdroid.core.log
import com.lib.lokdroid.core.logD
import com.lib.lokdroid.core.logE
import com.lib.lokdroid.core.logI
import com.lib.lokdroid.core.logV
import com.lib.lokdroid.core.logW
import com.lib.lokdroid.domain.model.Level

private data class LogAction(
    val label: String,
    val color: Color,
    val onClick: () -> Unit,
)

@Composable
internal fun SingleLoggingScreen(
    data: List<String>,
    onShowMultipleLogging: () -> Unit,
) {
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    val selectedItemValue = data[selectedItemIndex.coerceIn(0, data.lastIndex)]

    DemoScreenLayout(
        title = "Single log messages",
        subtitle = "One tap sends the selected value to a specific log level.",
        selectedScreen = DemoScreen.SingleLogging,
        onScreenSelected = { screen ->
            if (screen == DemoScreen.MultipleLogging) onShowMultipleLogging()
        },
    ) {
        DemoItemsList(
            modifier = Modifier.weight(1f),
            data = data,
            selectedItemIndex = selectedItemIndex,
            onItemSelected = { index -> selectedItemIndex = index },
        )
        LogActionsRow(
            actions = listOf(
                LogAction("Verbose", VerboseActionColor) { logV(selectedItemValue) },
                LogAction("Debug", DebugActionColor) { logD(selectedItemValue) },
                LogAction("Info", InfoActionColor) { logI(selectedItemValue) },
                LogAction("Warn", WarnActionColor) { logW(selectedItemValue) },
                LogAction("Error", ErrorActionColor) { logE(selectedItemValue) },
            ),
        )
    }
}

@Composable
internal fun MultipleLoggingScreen(
    data: List<String>,
    onShowSingleLogging: () -> Unit,
) {
    var selectedItemIndex by remember { mutableIntStateOf(0) }

    DemoScreenLayout(
        title = "Structured multi-line logs",
        subtitle = "Use the same sample set to build a formatted block message.",
        selectedScreen = DemoScreen.MultipleLogging,
        onScreenSelected = { screen ->
            if (screen == DemoScreen.SingleLogging) onShowSingleLogging()
        },
    ) {
        DemoItemsList(
            modifier = Modifier.weight(1f),
            data = data,
            selectedItemIndex = selectedItemIndex,
            onItemSelected = { index -> selectedItemIndex = index },
        )
        LogActionsRow(
            actions = listOf(
                LogAction("Multi V", VerboseActionColor) {
                    invokeMultipleLog(level = Level.Verbose, data = data)
                },
                LogAction("Multi D", DebugActionColor) {
                    invokeMultipleLog(level = Level.Debug, data = data)
                },
                LogAction("Multi I", InfoActionColor) {
                    invokeMultipleLog(level = Level.Info, data = data)
                },
                LogAction("Multi W", WarnActionColor) {
                    invokeMultipleLog(level = Level.Warn, data = data)
                },
                LogAction("Multi E", ErrorActionColor) {
                    invokeMultipleLog(level = Level.Error, data = data)
                },
            ),
        )
    }
}

@Composable
private fun DemoScreenLayout(
    title: String,
    subtitle: String,
    selectedScreen: DemoScreen,
    onScreenSelected: (DemoScreen) -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 6.dp,
            shadowElevation = 12.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "LoKdroid Compose Demo",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        ScreenToggleRow(
            selectedScreen = selectedScreen,
            onScreenSelected = onScreenSelected,
        )

        content()
    }
}

@Composable
private fun ScreenToggleRow(
    selectedScreen: DemoScreen,
    onScreenSelected: (DemoScreen) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        DemoScreen.entries.forEach { screen ->
            val isSelected = selectedScreen == screen

            FilledTonalButton(
                onClick = { onScreenSelected(screen) },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
            ) {
                Text(
                    text = when (screen) {
                        DemoScreen.SingleLogging -> "Single logs"
                        DemoScreen.MultipleLogging -> "Multi logs"
                    }
                )
            }
        }
    }
}

@Composable
private fun DemoItemsList(
    modifier: Modifier = Modifier,
    data: List<String>,
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(data) { index, value ->
                DemoItemRow(
                    value = value,
                    isSelected = index == selectedItemIndex,
                    onClick = { onItemSelected(index) },
                )
            }
        }
    }
}

@Composable
private fun DemoItemRow(
    value: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = if (isSelected) SelectedItemBackgroundColor else IdleItemBackgroundColor
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, shape = RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            modifier = Modifier.size(10.dp),
            shape = RoundedCornerShape(999.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        ) {}
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LogActionsRow(actions: List<LogAction>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        maxItemsInEachRow = 3,
    ) {
        actions.forEach { action ->
            Button(
                onClick = action.onClick,
                colors = ButtonDefaults.buttonColors(containerColor = action.color),
            ) {
                Text(text = action.label)
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}

private fun invokeMultipleLog(level: Level, data: List<String>) {
    log(level = level) {
        "Multiple log"()
        data.forEach { value -> value() }
    }
}

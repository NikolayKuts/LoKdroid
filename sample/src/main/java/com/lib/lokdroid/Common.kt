package com.lib.lokdroid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColumnScope.Items(
    data: List<String>,
    selectedItemIndex: Int,
    onItemClick: (index: Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.weight(1f)
    ) {
        itemsIndexed(data) { index, text ->
            ItemText(
                isSelected = selectedItemIndex == index,
                value = text,
                onClick = { onItemClick(index)}
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun ItemText(isSelected: Boolean, value: String, onClick: () -> Unit) {
    val color = if (isSelected) Color(0xFFE4EC96) else Color(0xFFF1DBB6)

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(color, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
        ,
        text = value,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ButtonsRow(
    selectedItemValue: String,
    forVerbose: Pair<String,(String) -> Unit>,
    forDebug: Pair<String,(String) -> Unit>,
    forInfo: Pair<String,(String) -> Unit>,
    forWarn: Pair<String,(String) -> Unit>,
    forError: Pair<String,(String) -> Unit>,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
        maxItemsInEachRow = 3
    ) {
        LogButton(
            text = forVerbose.first,
            color = Color(0xFF95C45E),
            onClick = { forVerbose.second(selectedItemValue) }
        )
        LogButton(
            text = forDebug.first,
            color = Color(0xFF3BA0CE),
            onClick = { forDebug.second(selectedItemValue) }
        )
        LogButton(
            text = forInfo.first,
            color = Color(0xFFECC348),
            onClick = { forInfo.second(selectedItemValue) }
        )
        LogButton(
            text = forWarn.first,
            color = Color(0xFF7658AC),
            onClick = { forWarn.second(selectedItemValue) }
        )
        LogButton(
            text = forError.first,
            color = Color(0xFFCE554C),
            onClick = { forError.second(selectedItemValue) }
        )
    }
}

@Composable
private fun LogButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        colors = ButtonDefaults.buttonColors(color),
        onClick = onClick,
    ) {
        Text(text = text)
    }
}
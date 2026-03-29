package com.lib.lokdroid.demoapp.sharedui

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

internal val LightColors = lightColorScheme(
    primary = Color(0xFF155E63),
    secondary = Color(0xFF4E6B2E),
    tertiary = Color(0xFF9A5E2E),
    background = Color(0xFFF6F3ED),
    surface = Color(0xFFFFFBF4),
)

internal val DarkColors = darkColorScheme(
    primary = Color(0xFF7BD0D6),
    secondary = Color(0xFFA9CF7F),
    tertiary = Color(0xFFE4A975),
    background = Color(0xFF121312),
    surface = Color(0xFF1A1D1B),
)

internal val VerboseActionColor = Color(0xFF5C9D4A)
internal val DebugActionColor = Color(0xFF3D8DB5)
internal val InfoActionColor = Color(0xFFC89A2F)
internal val WarnActionColor = Color(0xFF7D5CB8)
internal val ErrorActionColor = Color(0xFFC65B52)

internal val SelectedItemBackgroundColor = Color(0xFFDFF0D2)
internal val IdleItemBackgroundColor = Color(0xFFF2E7D6)

package com.svms.app.presentation.shared

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Brand Colors
val PurplePrimary = Color(0xFF5B2D8E)
val PurpleDark = Color(0xFF3D1A6B)
val PurpleLight = Color(0xFF7B4DB5)
val PurpleContainer = Color(0xFFEDE7F6)
val GoldAccent = Color(0xFFF5A623)
val GoldDark = Color(0xFFE09400)
val BackgroundGray = Color(0xFFF5F5F8)
val CardWhite = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF1A1A2E)
val TextSecondary = Color(0xFF6B6B80)
val BorderColor = Color(0xFFE0E0E8)
val ErrorRed = Color(0xFFE53935)
val SuccessGreen = Color(0xFF43A047)
val MinorOrange = Color(0xFFFF9800)
val MajorRed = Color(0xFFE53935)

private val SVMSColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = PurpleContainer,
    onPrimaryContainer = PurpleDark,
    secondary = GoldAccent,
    onSecondary = Color.White,
    background = BackgroundGray,
    onBackground = TextPrimary,
    surface = CardWhite,
    onSurface = TextPrimary,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun SVMSTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SVMSColorScheme,
        content = content
    )
}

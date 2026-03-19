package com.x.hrbeep.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Blue80       = Color(0xFF81D4FA)  // primary (light on dark)
private val Blue20       = Color(0xFF003549)  // onPrimary
private val BlueC30      = Color(0xFF004D67)  // primaryContainer
private val BlueC80      = Color(0xFFB3E5FC)  // onPrimaryContainer
private val Surface      = Color(0xFF0D1E2B)  // richer than default near-black
private val SurfaceVar   = Color(0xFF1C2E3A)
private val OnSurface    = Color(0xFFE3F2FD)  // blue-tinted near-white
private val OnSurfaceVar = Color(0xFF90A4AE)

private val DarkColors = darkColorScheme(
    primary             = Blue80,
    onPrimary           = Blue20,
    primaryContainer    = BlueC30,
    onPrimaryContainer  = BlueC80,
    surface             = Surface,
    surfaceVariant      = SurfaceVar,
    onSurface           = OnSurface,
    onSurfaceVariant    = OnSurfaceVar,
)

private val LightColors = lightColorScheme()

@Composable
fun HrBeepTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content,
    )
}

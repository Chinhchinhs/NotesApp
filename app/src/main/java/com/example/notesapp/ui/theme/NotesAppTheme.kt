package com.example.notesapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color.White,
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White
)

private val BaseTypography = Typography()

@Composable
fun NotesAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontSizeIndex: Int = 1, // 0 small, 1 medium, 2 large
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    // adjust typography body sizes (only simple adjustments)
    val typography = when (fontSizeIndex) {
        0 -> BaseTypography.copy(
            bodyLarge = BaseTypography.bodyLarge.copy(fontSize = 14.sp),
            bodyMedium = BaseTypography.bodyMedium.copy(fontSize = 12.sp)
        )
        2 -> BaseTypography.copy(
            bodyLarge = BaseTypography.bodyLarge.copy(fontSize = 20.sp),
            bodyMedium = BaseTypography.bodyMedium.copy(fontSize = 18.sp)
        )
        else -> BaseTypography
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        content = content
    )
}

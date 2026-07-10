package kr.co.call.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext


private val LightColorScheme = lightColorScheme(
    // Brand
    primary = Main,
    onPrimary = White,

    secondary = MainVariant1,
    onSecondary = White,

    // Background
    background = Background,
    onBackground = Black,

    // Surface
    surface = Background,
    onSurface = Black,

    surfaceVariant = SubGray2,
    onSurfaceVariant = Gray600,

    // Outline
    outline = Gray200,

    // Error
    error = SubRed,
    onError = White,
)

@Composable
fun CallFromAiTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalCallColors provides CallColors(),
        LocalCallTypography provides CallTypography(),
    ) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = Typography,
            content = content,
        )
    }
}

object CallTheme {

    val colors: CallColors
        @Composable
        get() = LocalCallColors.current

    val typography: CallTypography
        @Composable
        get() = LocalCallTypography.current
}
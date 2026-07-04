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

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Main,
    onPrimary = White,
    secondary = MainVariant1,
    onSecondary = White,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray600,
    outline = Gray200,
)

@Composable
fun CallFromAiTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalCallColors provides CallColors(),      // 아래 참고
        LocalCallTypography provides CallTypography(),
    ) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = Typography,
            content = content
        )
    }
}

object CallTheme {
    val colors: CallColors
        @Composable get() = LocalCallColors.current
    val typography: CallTypography
        @Composable get() = LocalCallTypography.current
}
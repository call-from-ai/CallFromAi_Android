package kr.co.call.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// ---------- Brand / Main ----------
val Main         = Color(0xFFFFA8CF)
val MainVariant1 = Color(0xFFFF8AAF)
val MainVariant2 = Color(0xFFFFDCE2)
val MainVariant3 = Color(0xFFFFA3BD)
val MainVariant4 = Color(0xFF391F15)

// ---------- Grayscale ----------
val Black = Color(0xFF1A1A1A)
val Gray900 = Color(0xFF2E2B2C)
val Gray800 = Color(0xFF5B5658)
val Gray600 = Color(0xFF7B7578)
val Gray400 = Color(0xFFA89EA1)
val Gray200 = Color(0xFFD9D2D4)
val Gray100 = Color(0xFFF0EDEE)
val White = Color(0xFFFFFFFF)

// ---------- Design System Colors ----------
@Immutable
data class CallColors(
    val main: Color = Main,
    val mainVariant1: Color = MainVariant1,
    val mainVariant2: Color = MainVariant2,
    val mainVariant3: Color = MainVariant3,
    val mainVariant4: Color = MainVariant4,
    val black: Color = Black,
    val gray900: Color = Gray900,
    val gray800: Color = Gray800,
    val gray600: Color = Gray600,
    val gray400: Color = Gray400,
    val gray200: Color = Gray200,
    val gray100: Color = Gray100,
    val white: Color = White,
)

val LocalCallColors = staticCompositionLocalOf { CallColors() }
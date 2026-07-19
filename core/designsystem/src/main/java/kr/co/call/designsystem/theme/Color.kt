package kr.co.call.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ---------- Brand / Main ----------
val Main = Color(0xFFFFA8CF)
val MainVariant1 = Color(0xFFFF8AAF)
val MainVariant2 = Color(0xFFFFDCE2)
val MainVariant3 = Color(0xFFFFA3BD)
val MainVariant4 = Color(0xFF3F1A13)
val MainVariant5Chat = Color(0xFFFFF2F4)

// ---------- Background ----------
val Background = Color(0xFFFFFCFC)

// ---------- Sub ----------
val SubGray = Color(0xFFE9E5E7)
val SubGray2 = Color(0xFFF7F3F3)

val SubRed = Color(0xFFFF0000)
val SubBlue = Color(0xFF4391FF)

val SubYellow=Color(0xFFFDE500)

val SubPressed = Color(0xFFED5988)
val SubPressed2 = Color(0xFFFCB4C1)
val SubPressed3 = Color(0xFF1F0A03)

// ---------- Grayscale ----------
val Black = Color(0xFF1A1A1A)
val Gray900 = Color(0xFF2E2B2C)
val Gray800 = Color(0xFF5B5658)
val Gray600 = Color(0xFF7B7578)
val Gray400 = Color(0xFFA89EA1)
val Gray200 = Color(0xFFD9D2D4)
val Gray100 = Color(0xFFF0EDEE)
val White = Color(0xFFFFFFFF)

// ---------- Gradient ----------
val MainGradient = Brush.horizontalGradient(
    listOf(
        Color(0xFFFFA8CF),
        Color(0xFFFFE0B8)
    ),
)

val ChatGradient = Brush.horizontalGradient(
    listOf(
        Color(0xFFFF789F),
        Color(0xFFFFA3BD)
    ),
)

val LandingGradient=Brush.linearGradient(
    colors=listOf(
        Color(0xFFFF789F),
        Color(0xFFFFA3BD)
    ),
    start=Offset.Zero,
    end=Offset.Infinite
)


val ChatGradientReverse = Brush.horizontalGradient(
    listOf(
        Color(0xFFFFA3BD),
        Color(0xFFFF789F)
    )
)

// ---------- Design System Colors ----------
@Immutable
data class CallColors(
    // Main
    val main: Color = Main,
    val mainVariant1: Color = MainVariant1,
    val mainVariant2: Color = MainVariant2,
    val mainVariant3: Color = MainVariant3,
    val mainVariant4: Color = MainVariant4,
    val mainVariant5Chat: Color = MainVariant5Chat,

    // Background
    val background: Color = Background,

    // Sub
    val subGray: Color = SubGray,
    val subGray2: Color = SubGray2,
    val subRed: Color = SubRed,
    val subBlue: Color = SubBlue,
    val subYellow: Color=SubYellow,
    val subPressed: Color = SubPressed,
    val subPressed2: Color = SubPressed2,
    val subPressed3: Color = SubPressed3,

    // Grayscale
    val black: Color = Black,
    val gray900: Color = Gray900,
    val gray800: Color = Gray800,
    val gray600: Color = Gray600,
    val gray400: Color = Gray400,
    val gray200: Color = Gray200,
    val gray100: Color = Gray100,
    val white: Color = White,

    // Gradient
    val mainGradient: Brush = MainGradient,
    val chatGradient: Brush = ChatGradient,
    val landingGradient: Brush=LandingGradient,
    val chatGradientReverse: Brush = ChatGradientReverse,
)

val LocalCallColors = staticCompositionLocalOf { CallColors() }
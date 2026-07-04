package kr.co.call.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.em
import kr.co.call.designsystem.R

private val LetterSpacing = (-0.02).em

val Pretendard = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_bold, FontWeight.Bold),
)

private fun pretendard(
    size: Int,
    lineHeightRatio: Float,
    weight: FontWeight,
) = TextStyle(
    fontFamily = Pretendard,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = (size * lineHeightRatio).sp,
    letterSpacing = LetterSpacing,
)

@Immutable
data class CallTypography(
    // Title (line height 140%)
    val titleLarge: TextStyle      = pretendard(32, 1.4f, FontWeight.Normal),
    val titleLargeBold: TextStyle  = pretendard(32, 1.4f, FontWeight.SemiBold),
    val titleMedium: TextStyle     = pretendard(28, 1.4f, FontWeight.Normal),
    val titleMediumBold: TextStyle = pretendard(28, 1.4f, FontWeight.SemiBold),
    val titleSmall: TextStyle      = pretendard(24, 1.4f, FontWeight.Normal),
    val titleSmallBold: TextStyle  = pretendard(24, 1.4f, FontWeight.SemiBold),

    // Body (line height 150%)
    val bodyLarge: TextStyle       = pretendard(20, 1.5f, FontWeight.Normal),
    val bodyLargeBold: TextStyle   = pretendard(20, 1.5f, FontWeight.SemiBold),
    val bodyMedium: TextStyle      = pretendard(16, 1.5f, FontWeight.Normal),
    val bodyMediumBold: TextStyle  = pretendard(16, 1.5f, FontWeight.SemiBold),
    val bodySmall: TextStyle       = pretendard(14, 1.5f, FontWeight.Normal),
    val bodySmallBold: TextStyle   = pretendard(14, 1.5f, FontWeight.SemiBold),

    // Others (line height 150%)
    val caption: TextStyle         = pretendard(12, 1.5f, FontWeight.Normal),
    val captionBold: TextStyle     = pretendard(12, 1.5f, FontWeight.SemiBold),
)

val LocalCallTypography = staticCompositionLocalOf { CallTypography() }



// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)
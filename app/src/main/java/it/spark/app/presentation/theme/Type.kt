package it.spark.app.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import it.spark.app.R

/**
 * Creates dynamic Google Sans Flex FontFamily with customized variable font axes.
 * - weight: 1..1000
 * - width: 25f..151f (100f is standard, 115f is expansive, 80f is condensed)
 * - roundness: 0f..100f (0f is geometric crisp ends, 100f is expressive pill/rounded terminals)
 * - grade: 0..100
 * - slant: -10f..0f
 */
@OptIn(ExperimentalTextApi::class)
fun flexFont(
    weight: Int = 400,
    width: Float = 100f,
    roundness: Float = 0f,
    grade: Int = 0,
    slant: Float = 0f
): FontFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(weight),
            FontVariation.width(width),
            FontVariation.grade(grade),
            FontVariation.slant(slant),
            FontVariation.Setting("ROND", roundness)
        )
    )
)

// Precomputed Expressive Variable Families for high performance & smart Compose skipping:
val FlexHeroNumericFont = flexFont(weight = 820, width = 115f, roundness = 100f)
val FlexHeadlineFont = flexFont(weight = 750, width = 108f, roundness = 80f)
val FlexSubheadingFont = flexFont(weight = 600, width = 102f, roundness = 60f)
val FlexChipLabelFont = flexFont(weight = 650, width = 105f, roundness = 60f)
val FlexBadgeAlertFont = flexFont(weight = 800, width = 110f, roundness = 80f, grade = 50)
val FlexStatValueFont = flexFont(weight = 780, width = 112f, roundness = 90f)

object ExpressiveType {
    val heroGiant = TextStyle(
        fontFamily = FlexHeroNumericFont,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp,
        lineHeight = 52.sp,
        letterSpacing = (-0.5).sp
    )
    val heroLarge = TextStyle(
        fontFamily = FlexHeroNumericFont,
        fontWeight = FontWeight.Normal,
        fontSize = 38.sp,
        lineHeight = 42.sp,
        letterSpacing = (-0.5).sp
    )
    val titleExpressive = TextStyle(
        fontFamily = FlexHeadlineFont,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.2).sp
    )
    val subtitleExpressive = TextStyle(
        fontFamily = FlexSubheadingFont,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 22.sp
    )
}

val GoogleSansFlexFontFamily = FontFamily(
    Font(R.font.google_sans_regular, FontWeight.Normal),
    Font(R.font.google_sans_medium, FontWeight.Medium)
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = GoogleSansFlexFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

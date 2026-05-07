package com.denser.june.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.denser.june.core.domain.model.enums.Fonts

@OptIn(ExperimentalTextApi::class)
@Composable
fun getAppFontFamily(fontName: String): FontFamily {
    return remember(fontName) {
        val bundled = Fonts.entries.find { it.fullName == fontName }
        if (bundled != null) {
            FontFamily(
                Font(
                    resId = bundled.font,
                    weight = FontWeight.Normal,
                    variationSettings = FontVariation.Settings(FontVariation.weight(400))
                ),
                Font(
                    resId = bundled.font,
                    weight = FontWeight.Medium,
                    variationSettings = FontVariation.Settings(FontVariation.weight(500))
                ),
                Font(
                    resId = bundled.font,
                    weight = FontWeight.SemiBold,
                    variationSettings = FontVariation.Settings(FontVariation.weight(600))
                ),
                Font(
                    resId = bundled.font,
                    weight = FontWeight.Bold,
                    variationSettings = FontVariation.Settings(FontVariation.weight(700))
                )
            )
        } else {
            val googleFont = GoogleFont(fontName.trim())
            FontFamily(
                Font(
                    googleFont = googleFont,
                    fontProvider = provider,
                    weight = FontWeight.Normal
                ),
                Font(
                    googleFont = googleFont,
                    fontProvider = provider,
                    weight = FontWeight.Bold
                ),
                Font(
                    googleFont = googleFont,
                    fontProvider = provider,
                    weight = FontWeight.Medium
                ),
                Font(
                    googleFont = googleFont,
                    fontProvider = provider,
                    weight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun provideTypography(
    scale: Float = 1f,
    font: String = "Google Sans Flex",
): Typography {

    val displayFont = getAppFontFamily(font)
    val bodyFont = getAppFontFamily(font)

    return Typography(
        displayLarge = TextStyle(
            fontFamily = displayFont,
            fontWeight = FontWeight.Bold,
            fontSize = 57.sp * scale,
            lineHeight = 64.sp * scale,
            letterSpacing = (-0.25).sp,
        ),
        displayMedium = TextStyle(
            fontFamily = displayFont,
            fontWeight = FontWeight.Bold,
            fontSize = 45.sp * scale,
            lineHeight = 52.sp * scale,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = displayFont,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp * scale,
            lineHeight = 44.sp * scale,
            letterSpacing = 0.sp
        ),

        headlineLarge = TextStyle(
            fontFamily = displayFont,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp * scale,
            lineHeight = 40.sp * scale,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = displayFont,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp * scale,
            lineHeight = 36.sp * scale,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = displayFont,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp * scale,
            lineHeight = 32.sp * scale,
            letterSpacing = 0.sp
        ),

        titleLarge = TextStyle(
            fontFamily = bodyFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp * scale,
            lineHeight = 28.sp * scale,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = bodyFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp * scale,
            lineHeight = 24.sp * scale,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = bodyFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp * scale,
            lineHeight = 20.sp * scale,
            letterSpacing = 0.1.sp
        ),

        bodyLarge = TextStyle(
            fontFamily = bodyFont,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp * scale,
            lineHeight = 24.sp * scale,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = bodyFont,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp * scale,
            lineHeight = 20.sp * scale,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = bodyFont,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp * scale,
            lineHeight = 16.sp * scale,
            letterSpacing = 0.4.sp
        ),

        labelLarge = TextStyle(
            fontFamily = bodyFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp * scale,
            lineHeight = 20.sp * scale,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = bodyFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp * scale,
            lineHeight = 16.sp * scale,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = bodyFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp * scale,
            lineHeight = 16.sp * scale,
            letterSpacing = 0.5.sp
        )
    )
}
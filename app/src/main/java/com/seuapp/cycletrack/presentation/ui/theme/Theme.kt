package com.seuapp.cycletrack.presentation.ui.theme

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.ui.text.TextStyle

/**
 * Sistema de Temas do CycleTrack - Material3 Compose
 * Clean-room design system prÃ³prio.
 */

// ============ Color Schemes ============

// Paleta derivada da logomarca CycleTrack (ver assets em res/drawable/logo_cycletrack.png)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFCC1262),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF9E3EC),
    onPrimaryContainer = Color(0xFF8F1C60),
    secondary = Color(0xFF4BAF4F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE4F3E5),
    onSecondaryContainer = Color(0xFF1C381D),
    tertiary = Color(0xFF3B82F6),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFDBEAFE),
    onTertiaryContainer = Color(0xFF1E3A8A),
    error = Color(0xFFEF4444),
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFFB91C1C),
    surface = Color.White,
    onSurface = Color(0xFF171717),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF525252),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF171717),
    outline = Color(0xFFE5E5E5),
    outlineVariant = Color(0xFFD4D4D4),
    inverseSurface = Color(0xFF171717),
    inverseOnSurface = Color(0xFFFAFAFA),
    inversePrimary = Color(0xFFE37FAA),
    scrim = Color.Black,
    surfaceTint = Color(0xFFCC1262)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE37FAA),
    onPrimary = Color(0xFF3D0E29),
    primaryContainer = Color(0xFF8F1C60),
    onPrimaryContainer = Color(0xFFF9E3EC),
    secondary = Color(0xFF93CF95),
    onSecondary = Color(0xFF1C381D),
    secondaryContainer = Color(0xFF347436),
    onSecondaryContainer = Color(0xFFE4F3E5),
    tertiary = Color(0xFF60A5FA),
    onTertiary = Color(0xFF1E3A8A),
    tertiaryContainer = Color(0xFF1E40AF),
    onTertiaryContainer = Color(0xFFDBEAFE),
    error = Color(0xFFF87171),
    onError = Color(0xFF14532D),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2),
    surface = Color(0xFF262626),
    onSurface = Color(0xFFFAFAFA),
    surfaceVariant = Color(0xFF404040),
    onSurfaceVariant = Color(0xFFA3A3A3),
    background = Color(0xFF0A0A0A),
    onBackground = Color(0xFFFAFAFA),
    outline = Color(0xFF525252),
    outlineVariant = Color(0xFF404040),
    inverseSurface = Color(0xFFF5F5F5),
    inverseOnSurface = Color(0xFF171717),
    inversePrimary = Color(0xFFCC1262),
    scrim = Color.Black,
    surfaceTint = Color(0xFFE37FAA)
)

// ============ Typography ============

private val Typography = androidx.compose.material3.Typography(
    displayLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = -0.02.sp
    ),
    displayMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = -0.01.sp
    ),
    displaySmall = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
    ),
    headlineLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = -0.01.sp
    ),
    headlineMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    headlineSmall = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    titleLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.015.sp
    ),
    titleSmall = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.01.sp
    ),
    bodyLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodySmall = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelSmall = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
    )
)

// ============ Font Family ============

// ============ Shapes ============

private val Shapes = androidx.compose.material3.Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
)

// ============ Theme Entry Point ============

@Composable
fun CycleTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    val context = LocalContext.current

    // Aplica status bar / navigation bar colors
    SideEffect {
        val activity = (context as? Activity) ?: return@SideEffect
        val window = activity.window
        val controller = WindowInsetsControllerCompat(window, view)
        controller.isAppearanceLightStatusBars = !darkTheme
        controller.isAppearanceLightNavigationBars = !darkTheme
        window.statusBarColor = colorScheme.background.toArgb()
        window.navigationBarColor = colorScheme.background.toArgb()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

// ============ Semantic Color Extensions ============

@Composable
fun CycleTrackColors(): CycleTrackColors {
    val colorScheme = MaterialTheme.colorScheme
    return remember(colorScheme) {
        CycleTrackColors(
            // Brand
            primary = colorScheme.primary,
            onPrimary = colorScheme.onPrimary,
            primaryContainer = colorScheme.primaryContainer,
            onPrimaryContainer = colorScheme.onPrimaryContainer,
            secondary = colorScheme.secondary,
            onSecondary = colorScheme.onSecondary,
            secondaryContainer = colorScheme.secondaryContainer,
            onSecondaryContainer = colorScheme.onSecondaryContainer,
            tertiary = colorScheme.tertiary,
            onTertiary = colorScheme.onTertiary,
            tertiaryContainer = colorScheme.tertiaryContainer,
            onTertiaryContainer = colorScheme.onTertiaryContainer,

            // Semantic
            success = Color(0xFF4BAF4F),
            onSuccess = Color.White,
            successContainer = Color(0xFFE4F3E5),
            onSuccessContainer = Color(0xFF1C381D),
            warning = Color(0xFFF59E0B),
            onWarning = Color(0xFF14532D),
            warningContainer = Color(0xFFFEF3C7),
            onWarningContainer = Color(0xFFB45309),
            error = colorScheme.error,
            onError = colorScheme.onError,
            errorContainer = colorScheme.errorContainer,
            onErrorContainer = colorScheme.onErrorContainer,
            info = Color(0xFF3B82F6),
            onInfo = Color.White,
            infoContainer = Color(0xFFDBEAFE),
            onInfoContainer = Color(0xFF1E3A8A),

            // Surface
            surface = colorScheme.surface,
            onSurface = colorScheme.onSurface,
            surfaceVariant = colorScheme.surfaceVariant,
            onSurfaceVariant = colorScheme.onSurfaceVariant,
            background = colorScheme.background,
            onBackground = colorScheme.onBackground,

            // Outline
            outline = colorScheme.outline,
            outlineVariant = colorScheme.outlineVariant,

            // Cycle Phases
            phaseMenstrual = Color(0xFFCC1262),
            phaseFollicular = Color(0xFF4BAF4F),
            phaseOvulation = Color(0xFFF59E0B),
            phaseLuteal = Color(0xFF3B82F6),

            // Symptom Categories
            categoryPhysical = Color(0xFF8F1C60),
            categoryPhysicalContainer = Color(0xFFF9E3EC),
            categoryEmotional = Color(0xFF3730A3),
            categoryEmotionalContainer = Color(0xFFE0E7FF),
            categoryDischarge = Color(0xFF166534),
            categoryDischargeContainer = Color(0xFFF0FDF4),
            categoryPain = Color(0xFFB91C1C),
            categoryPainContainer = Color(0xFFFEF2F2),
            categoryLifestyle = Color(0xFFB45309),
            categoryLifestyleContainer = Color(0xFFFEF3C7),

            // Pregnancy Trimesters
            trimester1 = Color(0xFF3B82F6),
            trimester2 = Color(0xFF4BAF4F),
            trimester3 = Color(0xFFCC1262),

            // Accent Teal (ilustracoes de sinais do corpo, modo parceiro)
            brandTeal = Color(0xFF1EC4BC),
            brandTealContainer = Color(0xFFDDF6F5),
            onBrandTealContainer = Color(0xFF103E3C),

            // Inverse
            inverseSurface = colorScheme.inverseSurface,
            inverseOnSurface = colorScheme.inverseOnSurface,
            inversePrimary = colorScheme.inversePrimary
        )
    }
}

/**
 * Cores semÃ¢nticas do CycleTrack.
 */
data class CycleTrackColors(
    // Brand
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,

    // Semantic
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,

    // Surface
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val background: Color,
    val onBackground: Color,

    // Outline
    val outline: Color,
    val outlineVariant: Color,

    // Cycle Phases
    val phaseMenstrual: Color,
    val phaseFollicular: Color,
    val phaseOvulation: Color,
    val phaseLuteal: Color,

    // Symptom Categories
    val categoryPhysical: Color,
    val categoryPhysicalContainer: Color,
    val categoryEmotional: Color,
    val categoryEmotionalContainer: Color,
    val categoryDischarge: Color,
    val categoryDischargeContainer: Color,
    val categoryPain: Color,
    val categoryPainContainer: Color,
    val categoryLifestyle: Color,
    val categoryLifestyleContainer: Color,

    // Pregnancy Trimesters
    val trimester1: Color,
    val trimester2: Color,
    val trimester3: Color,

    // Accent Teal
    val brandTeal: Color,
    val brandTealContainer: Color,
    val onBrandTealContainer: Color,

    // Inverse
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val inversePrimary: Color
) {
    // Helper para superfÃ­cie elevada
    val surfaceElevated1: Color = surface.copy(alpha = 0.95f)
    val surfaceElevated2: Color = surface.copy(alpha = 0.9f)
    val surfaceElevated3: Color = surface.copy(alpha = 0.85f)
}

// ============ Dimension Constants ============

object CycleTrackDimens {
    // Spacing
    val spacingXxs = 4.dp
    val spacingXs = 8.dp
    val spacingSm = 12.dp
    val spacingMd = 16.dp
    val spacingLg = 24.dp
    val spacingXl = 32.dp
    val spacingXxl = 48.dp

    // Corner Radius
    val radiusXs = 4.dp
    val radiusSm = 8.dp
    val radiusMd = 12.dp
    val radiusLg = 16.dp
    val radiusXl = 24.dp
    val radiusFull = 100.dp

    // Icon Sizes
    val iconXs = 16.dp
    val iconSm = 20.dp
    val iconMd = 24.dp
    val iconLg = 32.dp
    val iconXl = 48.dp

    // Avatar Sizes
    val avatarXs = 24.dp
    val avatarSm = 32.dp
    val avatarMd = 40.dp
    val avatarLg = 56.dp
    val avatarXl = 80.dp

    // Button Heights
    val buttonHeightSm = 40.dp
    val buttonHeightMd = 48.dp
    val buttonHeightLg = 56.dp

    // Input Heights
    val inputHeightSm = 40.dp
    val inputHeightMd = 48.dp
    val inputHeightLg = 56.dp

    // Card Elevation
    val elevationLevel0 = 0.dp
    val elevationLevel1 = 1.dp
    val elevationLevel2 = 3.dp
    val elevationLevel3 = 6.dp
    val elevationLevel4 = 12.dp

    // Animation Durations
    val animFast = 150
    val animMedium = 250
    val animSlow = 350
    val animExtraSlow = 500
}

// ============ Helper Extensions ============

@Composable
fun isDarkTheme(): Boolean {
    return MaterialTheme.colorScheme.surface.luminance() < 0.5f
}

@Composable
fun cyclePhaseColor(phase: String): Color {
    val colors = CycleTrackColors()
    return when (phase) {
        "menstrual" -> colors.phaseMenstrual
        "follicular" -> colors.phaseFollicular
        "ovulation" -> colors.phaseOvulation
        "luteal" -> colors.phaseLuteal
        else -> colors.onSurfaceVariant
    }
}

@Composable
fun symptomCategoryColor(category: String): Color {
    val colors = CycleTrackColors()
    return when (category) {
        "physical" -> colors.categoryPhysical
        "emotional" -> colors.categoryEmotional
        "discharge" -> colors.categoryDischarge
        "pain" -> colors.categoryPain
        "lifestyle" -> colors.categoryLifestyle
        else -> colors.onSurfaceVariant
    }
}

@Composable
fun symptomCategoryContainerColor(category: String): Color {
    val colors = CycleTrackColors()
    return when (category) {
        "physical" -> colors.categoryPhysicalContainer
        "emotional" -> colors.categoryEmotionalContainer
        "discharge" -> colors.categoryDischargeContainer
        "pain" -> colors.categoryPainContainer
        "lifestyle" -> colors.categoryLifestyleContainer
        else -> colors.surfaceVariant
    }
}

@Composable
fun trimesterColor(trimester: Int): Color {
    val colors = CycleTrackColors()
    return when (trimester) {
        1 -> colors.trimester1
        2 -> colors.trimester2
        3 -> colors.trimester3
        else -> colors.onSurfaceVariant
    }
}
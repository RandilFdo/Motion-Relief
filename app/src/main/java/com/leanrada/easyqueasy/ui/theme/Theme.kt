package com.leanrada.easyqueasy.ui.theme
import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
//    surfaceDim = surfaceDimLight,
//    surfaceBright = surfaceBrightLight,
//    surfaceContainerLowest = surfaceContainerLowestLight,
//    surfaceContainerLow = surfaceContainerLowLight,
//    surfaceContainer = surfaceContainerLight,
//    surfaceContainerHigh = surfaceContainerHighLight,
//    surfaceContainerHighest = surfaceContainerHighestLight,
)


private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
//    surfaceDim = surfaceDimLightMediumContrast,
//    surfaceBright = surfaceBrightLightMediumContrast,
//    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
//    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
//    surfaceContainer = surfaceContainerLightMediumContrast,
//    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
//    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
//    surfaceDim = surfaceDimLightHighContrast,
//    surfaceBright = surfaceBrightLightHighContrast,
//    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
//    surfaceContainerLow = surfaceContainerLowLightHighContrast,
//    surfaceContainer = surfaceContainerLightHighContrast,
//    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
//    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)



@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

@Composable
fun AppTheme(
    // Custom colors
    customAppBackgroundColor: Int? = null,
    customButtonBackgroundColor: Int? = null,
    content: @Composable() () -> Unit
) {
  // FORCE WHITE BACKGROUND WITH BLACK TEXT/BUTTONS - NO THEME DETECTION
  val customColor = Color(customAppBackgroundColor ?: 0xFFFFFFFF.toInt())
  val customButtonColor = Color(customButtonBackgroundColor ?: 0xFF000000.toInt())
  
  // Ensure no noisy logs in release
  
  // FORCE white background with black text by default, only change when manually customized
  val isDefaultWhite = customAppBackgroundColor == null || customAppBackgroundColor == 0xFFFFFFFF.toInt()
  val textColor = if (isDefaultWhite) Color.Black else (if (customColor.luminance() > 0.5f) Color.Black else Color.White)
  
  // ALWAYS use white background with black text/buttons by default
  val colorScheme = lightScheme.copy(
      background = customColor,
      surface = customColor,
      onBackground = textColor, // Black by default, smart when customized
      onSurface = textColor, // Black by default, smart when customized
      primary = customButtonColor,
      onPrimary = if (customButtonColor.luminance() > 0.5f) Color.Black else Color.White,
      primaryContainer = customButtonColor,
      onPrimaryContainer = if (customButtonColor.luminance() > 0.5f) Color.Black else Color.White,
      secondary = customButtonColor,
      onSecondary = if (customButtonColor.luminance() > 0.5f) Color.Black else Color.White,
      tertiary = customButtonColor,
      onTertiary = if (customButtonColor.luminance() > 0.5f) Color.Black else Color.White,
      error = Color.Red,
      onError = Color.White,
      surfaceVariant = customColor,
      onSurfaceVariant = textColor,
      outline = Color.Black,
      outlineVariant = Color.Gray,
      scrim = Color.Black.copy(alpha = 0.5f),
      inverseSurface = Color.Black,
      inversePrimary = Color.White
  )
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = android.graphics.Color.TRANSPARENT
      window.setBackgroundDrawableResource(android.R.color.transparent)
      // ALWAYS use light status bars (black text) by default, only change when manually customized
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isDefaultWhite || customColor.luminance() > 0.5f
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
  ) {
    androidx.compose.material3.Surface(
      color = colorScheme.background,
      contentColor = colorScheme.onBackground
    ) {
      content()
    }
  }
}


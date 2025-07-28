package com.bakhtiyor.testcase.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

data class ConfidenceDisplayInfo(
    val percentage: Int,
    val textColor: Color,
    val surfaceColor: Color,
    val borderStroke: BorderStroke?,
)

@Composable
fun rememberConfidenceDisplayInfo(
    confidence: Double,
    hasBorder: Boolean = false,
): ConfidenceDisplayInfo {
    val percentage = (confidence * 100).roundToInt()
    val themeColors = MaterialTheme.colorScheme

    val baseColor =
        remember(percentage, themeColors) {
            when {
                percentage >= 70 -> themeColors.primary
                percentage >= 40 -> themeColors.tertiary
                else -> themeColors.error
            }
        }

    val textColor = remember(baseColor) { baseColor }
    val surfaceColor = remember(baseColor) { baseColor.copy(alpha = 0.2f) }
    val borderStroke =
        remember(baseColor, hasBorder) {
            if (hasBorder) {
                BorderStroke(1.dp, baseColor.copy(alpha = 0.5f))
            } else {
                null
            }
        }

    return remember(percentage, textColor, surfaceColor, borderStroke) {
        ConfidenceDisplayInfo(
            percentage = percentage,
            textColor = textColor,
            surfaceColor = surfaceColor,
            borderStroke = borderStroke,
        )
    }
}

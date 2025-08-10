package ua.at.tsvetkov.bubbles

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
* Created by Alexandr Tsvetkov on 30.07.2025.
*/

/**
 * Объект данных, содержащий общие настройки внешнего вида и поведения Bubble.
 */
data class BubblesSettings(
    val arrowWidth: Dp = DEFAULT_ARROW_WIDTH,
    val arrowHeight: Dp = DEFAULT_ARROW_HEIGHT,
    val cornerRadius: Dp = DEFAULT_CORNER_RADIUS,
    val arrowTargetOffset: Dp = 0.dp,
    val backgroundColor: Color = DEFAULT_BACKGROUND_COLOR,
    val horizontalScreenPadding: Dp = DEFAULT_HORIZONTAL_SCREEN_PADDING,
    val verticalScreenPadding: Dp = DEFAULT_VERTICAL_SCREEN_PADDING,
    val scrimColor: Color = DEFAULT_SCRIM_COLOR,
    val dismissOnScrimClick: Boolean = false,
    val enterAnimationDurationMs: Int = DEFAULT_ANIMATION_DURATION_MS,
    val exitAnimationDurationMs: Int = DEFAULT_ANIMATION_DURATION_MS,
) {
    companion object {
        val DEFAULT_ARROW_WIDTH = 20.dp
        val DEFAULT_ARROW_HEIGHT = 10.dp
        val DEFAULT_CORNER_RADIUS = 8.dp
        val DEFAULT_BACKGROUND_COLOR = Color(0xFF424242)
        val DEFAULT_HORIZONTAL_SCREEN_PADDING = 16.dp
        val DEFAULT_VERTICAL_SCREEN_PADDING = 16.dp
        val DEFAULT_SCRIM_COLOR = Color(0x33000000)
        val DEFAULT_ANIMATION_DURATION_MS = 300
    }
}
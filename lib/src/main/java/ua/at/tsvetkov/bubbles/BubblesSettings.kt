package ua.at.tsvetkov.bubbles

/**
 * Created by Alexandr Tsvetkov on 28.07.2025.
 */

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Configuration settings for the appearance and behavior of bubbles.
 * Provides default values for various bubble properties.
 *
 * @property arrowWidth The width of the bubble's arrow.
 * @property arrowHeight The height (or length) of the bubble's arrow.
 * @property cornerRadius The corner radius for the bubble's body.
 * @property arrowTargetOffset An offset applied to the arrow's position along the axis of the target component's side.
 *                             For example, for [ArrowPosition.LEFT], this will shift the bubble vertically along the left edge of the target.
 * @property backgroundColor The background color of the bubble.
 * @property bubbleBorderColor The color of the bubble's border.
 * @property bubbleBorderWidth The width of the bubble's border.
 * @property horizontalScreenPadding Horizontal padding from the screen edges to constrain the bubble.
 * @property verticalScreenPadding Vertical padding from the screen edges to constrain the bubble.
 * @property scrimColor The color of the scrim background displayed behind the bubble.
 * @property dismissOnScrimClick If true, the bubble will be dismissed when the scrim is clicked.
 * @property enterAnimationDurationMs Duration of the enter animation in milliseconds.
 * @property exitAnimationDurationMs Duration of the exit animation in milliseconds.
 */
data class BubblesSettings(
    val arrowWidth: Dp = DEFAULT_ARROW_WIDTH,
    val arrowHeight: Dp = DEFAULT_ARROW_HEIGHT,
    val cornerRadius: Dp = DEFAULT_CORNER_RADIUS,
    val arrowTargetOffset: Dp = 0.dp,
    val backgroundColor: Color = DEFAULT_BACKGROUND_COLOR,
    val bubbleBorderColor: Color = DEFAULT_BORDER_COLOR,
    val bubbleBorderWidth: Dp = DEFAULT_BORDER_WIDTH,
    val horizontalScreenPadding: Dp = DEFAULT_HORIZONTAL_SCREEN_PADDING,
    val verticalScreenPadding: Dp = DEFAULT_VERTICAL_SCREEN_PADDING,
    val scrimColor: Color = DEFAULT_SCRIM_COLOR,
    val dismissOnScrimClick: Boolean = false,
    val enterAnimationDurationMs: Int = DEFAULT_ANIMATION_DURATION_MS,
    val exitAnimationDurationMs: Int = DEFAULT_ANIMATION_DURATION_MS,
) {
    companion object {
        /** Default width of the bubble's arrow. */
        val DEFAULT_ARROW_WIDTH = 20.dp

        /** Default height (length) of the bubble's arrow. */
        val DEFAULT_ARROW_HEIGHT = 10.dp

        /** Default corner radius for the bubble's body. */
        val DEFAULT_CORNER_RADIUS = 8.dp

        /** Default background color of the bubble. */
        val DEFAULT_BACKGROUND_COLOR = Color(0xFF424242)

        /** Default color of the bubble's border (transparent). */
        val DEFAULT_BORDER_COLOR = Color.Transparent

        /** Default width of the bubble's border. */
        val DEFAULT_BORDER_WIDTH = 0.dp

        /** Default horizontal padding from the screen edges. */
        val DEFAULT_HORIZONTAL_SCREEN_PADDING = 16.dp

        /** Default vertical padding from the screen edges. */
        val DEFAULT_VERTICAL_SCREEN_PADDING = 16.dp

        /** Default color for the scrim background. */
        val DEFAULT_SCRIM_COLOR = Color(0x33000000)

        /** Default duration for enter and exit animations in milliseconds. */
        const val DEFAULT_ANIMATION_DURATION_MS = 300
    }
}
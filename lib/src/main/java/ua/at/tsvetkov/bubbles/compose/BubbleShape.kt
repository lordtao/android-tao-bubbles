package ua.at.tsvetkov.bubbles.compose

/**
 * Created by Alexandr Tsvetkov on 23.10.2025.
 */

import android.graphics.Path
import android.graphics.RectF
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

/**
 * A [Shape] implementation for drawing a bubble with an arrow.
 * The shape consists of a rectangular body with rounded corners and a triangular arrow
 * attached to one of its sides.
 *
 * @param arrowPosition Specifies which side of the bubble body the arrow will be on ([ArrowPosition.LEFT],
 * [ArrowPosition.RIGHT], [ArrowPosition.TOP], [ArrowPosition.BOTTOM]).
 * @param arrowOffset The offset of the arrow's base center from the center of the corresponding side
 * of the bubble body.
 * For [ArrowPosition.TOP] and [ArrowPosition.BOTTOM], this is a horizontal offset.
 * For [ArrowPosition.LEFT] and [ArrowPosition.RIGHT], this is a vertical offset.
 * @param arrowWidth The width of the arrow's base.
 * @param arrowHeight The length (height) of the arrow from its base to its tip.
 * @param cornerRadius The corner radius for the bubble body.
 */
@Suppress("MagicNumber", "LongMethod")
class BubbleShape(
    private val arrowPosition: ArrowPosition,
    private val arrowOffset: Dp,
    private val arrowWidth: Dp,
    private val arrowHeight: Dp,
    private val cornerRadius: Dp
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path()

        val arrowWidthPx = with(density) { arrowWidth.toPx() }
        val arrowHeightPx = with(density) { arrowHeight.toPx() }
        val arrowOffsetPx = with(density) { arrowOffset.toPx() }
        val cornerRadiusPx = with(density) { cornerRadius.toPx() }

        val r = cornerRadiusPx.coerceAtLeast(0f)

        val bodyRectLeft: Float = when (arrowPosition) {
            ArrowPosition.RIGHT -> arrowHeightPx
            else -> 0f
        }
        val bodyRectTop: Float = when (arrowPosition) {
            ArrowPosition.BOTTOM -> arrowHeightPx
            else -> 0f
        }
        val bodyRectRight: Float = when (arrowPosition) {
            ArrowPosition.LEFT -> size.width - arrowHeightPx
            else -> size.width
        }
        val bodyRectBottom: Float = when (arrowPosition) {
            ArrowPosition.TOP -> size.height - arrowHeightPx
            else -> size.height
        }

        val x0 = bodyRectLeft
        val y0 = bodyRectTop
        val x1 = bodyRectRight
        val y1 = bodyRectBottom

        val arrowHip1X: Float // First point
        val arrowHip1Y: Float
        val arrowHip2X: Float // Second point
        val arrowHip2Y: Float
        val arrowTipX: Float
        val arrowTipY: Float

        when (arrowPosition) {
            ArrowPosition.BOTTOM -> {
                arrowHip1X = arrowOffsetPx - arrowWidthPx / 2
                arrowHip1Y = y0
                arrowTipX = arrowOffsetPx
                arrowTipY = 0f
                arrowHip2X = arrowOffsetPx + arrowWidthPx / 2
                arrowHip2Y = y0
            }

            ArrowPosition.TOP -> {
                arrowHip1X = arrowOffsetPx - arrowWidthPx / 2
                arrowHip1Y = y1
                arrowTipX = arrowOffsetPx
                arrowTipY = size.height
                arrowHip2X = arrowOffsetPx + arrowWidthPx / 2
                arrowHip2Y = y1
            }

            ArrowPosition.LEFT -> {
                arrowHip1X = x1
                arrowHip1Y = arrowOffsetPx - arrowWidthPx / 2
                arrowTipX = size.width
                arrowTipY = arrowOffsetPx
                arrowHip2X = x1
                arrowHip2Y = arrowOffsetPx + arrowWidthPx / 2
            }

            ArrowPosition.RIGHT -> {
                arrowHip1X = x0
                arrowHip1Y = arrowOffsetPx + arrowWidthPx / 2
                arrowTipX = 0f
                arrowTipY = arrowOffsetPx
                arrowHip2X = x0
                arrowHip2Y = arrowOffsetPx - arrowWidthPx / 2
            }
        }

        when (arrowPosition) {
            ArrowPosition.BOTTOM -> {
                // Начинаем с точки после завершения верхнего левого скругления (X0 + R, Y0)
                path.moveTo(x0 + r, y0)

                // 1. Верхняя линия (левый сегмент) -> до левого бедра стрелки
                path.lineTo(arrowHip1X, y0)

                // 2. Стрелка
                path.lineTo(arrowTipX, arrowTipY)
                path.lineTo(arrowHip2X, y0)

                // 3. Верхняя линия (правый сегмент) -> до начала правого верхнего скругления (X1 - R, Y0)
                path.lineTo(x1 - r, y0)

                // 4. Правое верхнее скругление (270° -> 0°)
                path.arcTo(RectF(x1 - 2 * r, y0, x1, y0 + 2 * r), 270f, 90f)

                // 5. Правая линия -> до начала правого нижнего скругления (X1, Y1 - R)
                path.lineTo(x1, y1 - r)

                // 6. Правое нижнее скругление (0° -> 90°)
                path.arcTo(RectF(x1 - 2 * r, y1 - 2 * r, x1, y1), 0f, 90f)

                // 7. Нижняя линия -> до начала левого нижнего скругления (X0 + R, Y1)
                path.lineTo(x0 + r, y1)

                // 8. Левое нижнее скругление (90° -> 180°)
                path.arcTo(RectF(x0, y1 - 2 * r, x0 + 2 * r, y1), 90f, 90f)

                // 9. Левая линия -> до начала левого верхнего скругления (X0, Y0 + R)
                path.lineTo(x0, y0 + r)

                // 10. Левое верхнее скругление (180° -> 270°), замыкая путь.
                path.arcTo(RectF(x0, y0, x0 + 2 * r, y0 + 2 * r), 180f, 90f)
            }

            ArrowPosition.TOP -> {
                // Начинаем с точки после завершения верхнего левого скругления (X0 + R, Y0)
                path.moveTo(x0 + r, y0)

                // 1. Верхняя линия -> до начала правого верхнего скругления (X1 - R, Y0)
                path.lineTo(x1 - r, y0)

                // 2. Правое верхнее скругление (270° -> 0°)
                path.arcTo(RectF(x1 - 2 * r, y0, x1, y0 + 2 * r), 270f, 90f)

                // 3. Правая линия -> до начала правого нижнего скругления (X1, Y1 - R)
                path.lineTo(x1, y1 - r)

                // 4. Правое нижнее скругление (0° -> 90°), заканчивается перед стрелкой (X1 - R, Y1)
                path.arcTo(RectF(x1 - 2 * r, y1 - 2 * r, x1, y1), 0f, 90f)

                // 5. Нижняя линия (правый сегмент) -> до правого бедра стрелки
                path.lineTo(arrowHip2X, y1)

                // 6. Стрелка
                path.lineTo(arrowTipX, arrowTipY)
                path.lineTo(arrowHip1X, y1)

                // 7. Нижняя линия (левый сегмент) -> до начала левого нижнего скругления (X0 + R, Y1)
                path.lineTo(x0 + r, y1)

                // 8. Левое нижнее скругление (90° -> 180°)
                path.arcTo(RectF(x0, y1 - 2 * r, x0 + 2 * r, y1), 90f, 90f)

                // 9. Левая линия -> до начала левого верхнего скругления (X0, Y0 + R)
                path.lineTo(x0, y0 + r)

                // 10. Левое верхнее скругление (180° -> 270°), замыкая путь.
                path.arcTo(RectF(x0, y0, x0 + 2 * r, y0 + 2 * r), 180f, 90f)
            }

            ArrowPosition.LEFT -> {
                // Начинаем с точки после завершения верхнего левого скругления (X0 + R, Y0)
                path.moveTo(x0 + r, y0)

                // 1. Верхняя линия -> до начала правого верхнего скругления (X1 - R, Y0)
                path.lineTo(x1 - r, y0)

                // 2. Правое верхнее скругление (270° -> 0°), заканчивается перед стрелкой (X1, Y0 + R)
                path.arcTo(RectF(x1 - 2 * r, y0, x1, y0 + 2 * r), 270f, 90f)

                // 3. Правая линия (верхний сегмент) -> до верхнего бедра стрелки
                path.lineTo(x1, arrowHip1Y)

                // 4. Стрелка
                path.lineTo(arrowTipX, arrowTipY)
                path.lineTo(x1, arrowHip2Y)

                // 5. Правая линия (нижний сегмент) -> до начала правого нижнего скругления (X1, Y1 - R)
                path.lineTo(x1, y1 - r)

                // 6. Правое нижнее скругление (0° -> 90°)
                path.arcTo(RectF(x1 - 2 * r, y1 - 2 * r, x1, y1), 0f, 90f)

                // 7. Нижняя линия -> до начала левого нижнего скругления (X0 + R, Y1)
                path.lineTo(x0 + r, y1)

                // 8. Левое нижнее скругление (90° -> 180°)
                path.arcTo(RectF(x0, y1 - 2 * r, x0 + 2 * r, y1), 90f, 90f)

                // 9. Левая линия -> до начала левого верхнего скругления (X0, Y0 + R)
                path.lineTo(x0, y0 + r)

                // 10. Левое верхнее скругление (180° -> 270°), замыкая путь.
                path.arcTo(RectF(x0, y0, x0 + 2 * r, y0 + 2 * r), 180f, 90f)
            }

            ArrowPosition.RIGHT -> {
                // Начинаем с точки после завершения верхнего левого скругления (X0 + R, Y0)
                path.moveTo(x0 + r, y0)

                // 1. Верхняя линия -> до начала правого верхнего скругления (X1 - R, Y0)
                path.lineTo(x1 - r, y0)

                // 2. Правое верхнее скругление (270° -> 0°)
                path.arcTo(RectF(x1 - 2 * r, y0, x1, y0 + 2 * r), 270f, 90f)

                // 3. Правая линия -> до начала правого нижнего скругления (X1, Y1 - R)
                path.lineTo(x1, y1 - r)

                // 4. Правое нижнее скругление (0° -> 90°)
                path.arcTo(RectF(x1 - 2 * r, y1 - 2 * r, x1, y1), 0f, 90f)

                // 5. Нижняя линия -> до начала левого нижнего скругления (X0 + R, Y1)
                path.lineTo(x0 + r, y1)

                // 6. Левое нижнее скругление (90° -> 180°), заканчивается перед стрелкой (X0, Y1 - R)
                path.arcTo(RectF(x0, y1 - 2 * r, x0 + 2 * r, y1), 90f, 90f)

                // 7. Левая линия (нижний сегмент) -> до нижнего бедра стрелки
                path.lineTo(x0, arrowHip1Y)

                // 8. Стрелка
                path.lineTo(arrowTipX, arrowTipY)
                path.lineTo(x0, arrowHip2Y)

                // 9. Левая линия (верхний сегмент) -> до начала левого верхнего скругления (X0, Y0 + R)
                path.lineTo(x0, y0 + r)

                // 10. Левое верхнее скругление (180° -> 270°), замыкая путь.
                path.arcTo(RectF(x0, y0, x0 + 2 * r, y0 + 2 * r), 180f, 90f)
            }
        }

        path.close()
        return Outline.Generic(path.asComposePath())
    }
}

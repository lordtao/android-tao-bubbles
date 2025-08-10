package ua.at.tsvetkov.bubbles

/**
 * Created by Alexandr Tsvetkov on 28.07.2025.
 */
import android.graphics.Path
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ua.at.tsvetkov.bubbles.BubblesSettings.Companion.DEFAULT_ANIMATION_DURATION_MS
import ua.at.tsvetkov.bubbles.BubblesSettings.Companion.DEFAULT_ARROW_HEIGHT
import ua.at.tsvetkov.bubbles.BubblesSettings.Companion.DEFAULT_ARROW_WIDTH
import ua.at.tsvetkov.bubbles.BubblesSettings.Companion.DEFAULT_BACKGROUND_COLOR
import ua.at.tsvetkov.bubbles.BubblesSettings.Companion.DEFAULT_BORDER_COLOR
import ua.at.tsvetkov.bubbles.BubblesSettings.Companion.DEFAULT_BORDER_WIDTH
import ua.at.tsvetkov.bubbles.BubblesSettings.Companion.DEFAULT_CORNER_RADIUS
import ua.at.tsvetkov.bubbles.BubblesSettings.Companion.DEFAULT_HORIZONTAL_SCREEN_PADDING
import ua.at.tsvetkov.bubbles.BubblesSettings.Companion.DEFAULT_SCRIM_COLOR
import ua.at.tsvetkov.bubbles.BubblesSettings.Companion.DEFAULT_VERTICAL_SCREEN_PADDING
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Path as ComposePath

/**
 * Пользовательская форма для пузыря с хвостиком
 */
private class BubbleShape(
    private val arrowPosition: ArrowPosition,
    private val arrowOffset: Dp,
    private val arrowWidth: Dp,
    private val arrowHeight: Dp,
    private val cornerRadius: Dp,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path() // android.graphics.Path

        val arrowWidthPx = with(density) { arrowWidth.toPx() }
        val arrowHeightPx = with(density) { arrowHeight.toPx() }
        val arrowOffsetPx = with(density) { arrowOffset.toPx() }
        val cornerRadiusPx = with(density) { cornerRadius.toPx() }

        val bubbleRectLeft: Float
        val bubbleRectTop: Float
        val bubbleRectRight: Float
        val bubbleRectBottom: Float

        when (arrowPosition) {
            ArrowPosition.LEFT -> {
                bubbleRectLeft = 0f
                bubbleRectTop = 0f
                bubbleRectRight = size.width - arrowHeightPx
                bubbleRectBottom = size.height
            }

            ArrowPosition.RIGHT -> {
                bubbleRectLeft = arrowHeightPx
                bubbleRectTop = 0f
                bubbleRectRight = size.width
                bubbleRectBottom = size.height
            }

            ArrowPosition.TOP -> {
                bubbleRectLeft = 0f
                bubbleRectTop = 0f
                bubbleRectRight = size.width
                bubbleRectBottom = size.height - arrowHeightPx
            }

            ArrowPosition.BOTTOM -> {
                bubbleRectLeft = 0f
                bubbleRectTop = arrowHeightPx
                bubbleRectRight = size.width
                bubbleRectBottom = size.height
            }
        }

        // Bubble body
        path.addRoundRect(
            bubbleRectLeft, bubbleRectTop, bubbleRectRight, bubbleRectBottom,
            cornerRadiusPx, cornerRadiusPx, Path.Direction.CW
        )

        // Draw arrow
        when (arrowPosition) {
            ArrowPosition.LEFT -> {
                path.moveTo(bubbleRectRight, arrowOffsetPx - arrowWidthPx / 2) // Верхняя точка основания стрелки
                path.lineTo(bubbleRectRight, arrowOffsetPx + arrowWidthPx / 2) // Нижняя точка основания стрелки
                path.lineTo(size.width, arrowOffsetPx) // Кончик стрелки (на правом краю общей формы)
                path.close()
            }

            ArrowPosition.RIGHT -> {
                path.moveTo(bubbleRectLeft, arrowOffsetPx + arrowWidthPx / 2) // Нижняя точка основания стрелки (на левом краю тела пузыря)
                path.lineTo(bubbleRectLeft, arrowOffsetPx - arrowWidthPx / 2) // Верхняя точка основания стрелки
                path.lineTo(0f, arrowOffsetPx) // Кончик стрелки (на левом краю общей формы)
                path.close()
            }

            ArrowPosition.TOP -> {
                path.moveTo(arrowOffsetPx - arrowWidthPx / 2, bubbleRectBottom) // Левая точка основания стрелки
                path.lineTo(arrowOffsetPx + arrowWidthPx / 2, bubbleRectBottom) // Правая точка основания стрелки
                path.lineTo(arrowOffsetPx, size.height) // Кончик стрелки (на нижнем краю общей формы)
                path.close()
            }

            ArrowPosition.BOTTOM -> {
                path.moveTo(arrowOffsetPx + arrowWidthPx / 2, bubbleRectTop) // Правая точка основания стрелки (на верхном краю тела пузыря)
                path.lineTo(arrowOffsetPx - arrowWidthPx / 2, bubbleRectTop) // Левая точка основания стрелки
                path.lineTo(arrowOffsetPx, 0f) // Кончик стрелки (на верхнем краю общей формы)
                path.close()
            }
        }
        return Outline.Generic(path.asComposePath())
    }
}

@Composable
fun Bubble(
    modifier: Modifier = Modifier,
    controller: BubbleShowController,
    bubbleData: BubbleData,
    isVisible: Boolean = true,
) {
    Bubble(
        modifier = modifier,
        id = bubbleData.id,
        targetComponentRect = controller.currentTargetRect,
        arrowPosition = bubbleData.arrowPosition,
        arrowWidth = controller.settings.arrowWidth,
        arrowHeight = controller.settings.arrowHeight,
        cornerRadius = controller.settings.cornerRadius,
        backgroundColor = controller.settings.backgroundColor,
        bubbleBorderColor = controller.settings.bubbleBorderColor,
        bubbleBorderWidth = controller.settings.bubbleBorderWidth,
        horizontalScreenPadding = controller.settings.horizontalScreenPadding,
        verticalScreenPadding = controller.settings.verticalScreenPadding,
        scrimColor = controller.settings.scrimColor,
        dismissOnScrimClick = controller.settings.dismissOnScrimClick,
        onDismissRequest = { controller.showNext() },
        arrowTargetOffset = controller.settings.arrowTargetOffset,
        enterAnimationDurationMs = controller.settings.enterAnimationDurationMs,
        exitAnimationDurationMs = controller.settings.exitAnimationDurationMs,
        isVisible = isVisible,
        content = bubbleData.content
    )
}

/**
 * Компонент Bubble с хвостиком, позиционируемый относительно целевого Composable.
 * Перегруженная функция, принимающая объекты BubblesSettings и BubbleData.
 */
@Composable
fun Bubble(
    modifier: Modifier = Modifier,
    settings: BubblesSettings = BubblesSettings(),
    bubbleData: BubbleData,
    targetComponentRect: Rect?,
    onDismissRequest: () -> Unit = {},
    isVisible: Boolean = true,
) {
    Bubble(
        modifier = modifier,
        id = bubbleData.id,
        targetComponentRect = targetComponentRect,
        arrowPosition = bubbleData.arrowPosition,
        arrowWidth = settings.arrowWidth,
        arrowHeight = settings.arrowHeight,
        cornerRadius = settings.cornerRadius,
        backgroundColor = settings.backgroundColor,
        bubbleBorderColor = settings.bubbleBorderColor,
        bubbleBorderWidth = settings.bubbleBorderWidth,
        horizontalScreenPadding = settings.horizontalScreenPadding,
        verticalScreenPadding = settings.verticalScreenPadding,
        scrimColor = settings.scrimColor,
        dismissOnScrimClick = settings.dismissOnScrimClick,
        onDismissRequest = onDismissRequest,
        arrowTargetOffset = settings.arrowTargetOffset,
        enterAnimationDurationMs = settings.enterAnimationDurationMs,
        exitAnimationDurationMs = settings.exitAnimationDurationMs,
        isVisible = isVisible,
        content = bubbleData.content
    )
}

/**
 * Компонент Bubble с хвостиком, позиционируемый относительно целевого Composable.
 *
 * @param modifier Модификатор для Bubble.
 * @param id Уникальный ключ для идентификации пузыря, используется для анимации.
 * @param targetComponentRect Прямоугольник, представляющий границы целевого элемента в оконных координатах.
 * @param arrowPosition Позиция хвостика Bubble (LEFT, RIGHT, TOP, BOTTOM) относительно целевого элемента.
 * @param arrowWidth Ширина основания хвостика.
 * @param arrowHeight Длина хвостика.
 * @param cornerRadius Радиус закругления углов Bubble.
 * @param backgroundColor Цвет фона Bubble.
 * @param bubbleBorderColor Цвет каймы Bubble.
 * @param bubbleBorderWidth Ширина каймы Bubble.
 * @param horizontalScreenPadding Отступ от левой и правой границ экрана.
 * @param verticalScreenPadding Отступ от верхней и нижней границ экрана.
 * @param scrimColor Цвет полупрозрачного фона под пузырем (включает прозрачность).
 * @param dismissOnScrimClick Если true, нажатие на полупрозрачный фон скроет пузырь.
 * @param onDismissRequest Колбэк, вызываемый при запросе на скрытие пузыря.
 * @param arrowTargetOffset Смещение стрелки относительно центра целевого компонента.
 * Положительное значение смещает стрелку от целевого компонента,
 * отрицательное - к нему.
 * @param enterAnimationDurationMs Продолжительность анимации появления пузыря.
 * @param exitAnimationDurationMs Продолжительность анимации исчезновения пузыря.
 * @param isVisible Управляет видимостью пузыря.
 * @param content Содержимое Bubble.
 */
@Composable
fun Bubble(
    modifier: Modifier = Modifier,
    id: String,
    targetComponentRect: Rect?,
    arrowPosition: ArrowPosition,
    arrowWidth: Dp = DEFAULT_ARROW_WIDTH,
    arrowHeight: Dp = DEFAULT_ARROW_HEIGHT,
    cornerRadius: Dp = DEFAULT_CORNER_RADIUS,
    backgroundColor: Color = DEFAULT_BACKGROUND_COLOR,
    bubbleBorderColor: Color = DEFAULT_BORDER_COLOR,
    bubbleBorderWidth: Dp = DEFAULT_BORDER_WIDTH,
    horizontalScreenPadding: Dp = DEFAULT_HORIZONTAL_SCREEN_PADDING,
    verticalScreenPadding: Dp = DEFAULT_VERTICAL_SCREEN_PADDING,
    scrimColor: Color = DEFAULT_SCRIM_COLOR,
    dismissOnScrimClick: Boolean = false,
    onDismissRequest: () -> Unit = {},
    arrowTargetOffset: Dp = 0.dp,
    enterAnimationDurationMs: Int = DEFAULT_ANIMATION_DURATION_MS,
    exitAnimationDurationMs: Int = DEFAULT_ANIMATION_DURATION_MS,
    isVisible: Boolean = true,
    content: @Composable (onActionClick: () -> Unit) -> Unit,
) {
    if (targetComponentRect == null) return

    val bubbleTransitionState = remember(id) { MutableTransitionState(false) }
    bubbleTransitionState.targetState = isVisible

    val showScrim = bubbleTransitionState.currentState || bubbleTransitionState.targetState

    if (showScrim) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(scrimColor)
                .clickable(enabled = dismissOnScrimClick) {
                    onDismissRequest()
                }
        ) {
            AnimatedVisibility(
                visibleState = bubbleTransitionState,
                enter = if (enterAnimationDurationMs == 0) EnterTransition.None else (
                        fadeIn(animationSpec = tween(durationMillis = enterAnimationDurationMs)) +
                                scaleIn(animationSpec = tween(durationMillis = enterAnimationDurationMs), initialScale = 0.8f)
                        ),
                exit = if (exitAnimationDurationMs == 0) ExitTransition.None else (
                        fadeOut(animationSpec = tween(durationMillis = exitAnimationDurationMs)) +
                                scaleOut(animationSpec = tween(durationMillis = exitAnimationDurationMs), targetScale = 0.8f)
                        )
            ) {
                val density = LocalDensity.current
                val windowInfo = LocalWindowInfo.current
                val screenWidth = with(density) { windowInfo.containerSize.width.toDp() }
                val screenHeight = with(density) { windowInfo.containerSize.height.toDp() }

                val statusBarHeightPx = WindowInsets.statusBars.getTop(density)

                // SubcomposeLayout for measuring and positioning the bubble itself
                SubcomposeLayout(modifier = Modifier.fillMaxSize()) { constraints ->
                    // 1. Measure the Bubble content to determine its dimensions
                    val horizontalContentPaddingPx =
                        with(density) { cornerRadius.toPx() * 2 + if (arrowPosition == ArrowPosition.LEFT || arrowPosition == ArrowPosition.RIGHT) arrowHeight.toPx() else 0f }
                    val verticalContentPaddingPx =
                        with(density) { cornerRadius.toPx() * 2 + if (arrowPosition == ArrowPosition.TOP || arrowPosition == ArrowPosition.BOTTOM) arrowHeight.toPx() else 0f }

                    val contentMeasurable = subcompose("bubbleContent") { content(onDismissRequest) }.first()
                    val contentPlaceable = contentMeasurable.measure(
                        Constraints(
                            minWidth = 0,
                            minHeight = 0,
                            maxWidth = (constraints.maxWidth - horizontalContentPaddingPx).roundToInt().coerceAtLeast(0),
                            maxHeight = (constraints.maxHeight - verticalContentPaddingPx).roundToInt().coerceAtLeast(0)
                        )
                    )

                    val measuredContentWidthPx = contentPlaceable.width.toFloat()
                    val measuredContentHeightPx = contentPlaceable.height.toFloat()

                    val bubbleBodyWidthPx = (measuredContentWidthPx + with(density) { cornerRadius.toPx() * 2 })
                    val bubbleBodyHeightPx = (measuredContentHeightPx + with(density) { cornerRadius.toPx() * 2 })

                    val constrainedBubbleFullWidthPx = (bubbleBodyWidthPx + when (arrowPosition) {
                        ArrowPosition.LEFT, ArrowPosition.RIGHT -> with(density) { arrowHeight.toPx() }
                        else -> 0f
                    }).coerceAtMost(with(density) { screenWidth.toPx() - horizontalScreenPadding.toPx() * 2 })

                    val constrainedBubbleFullHeightPx = (bubbleBodyHeightPx + when (arrowPosition) {
                        ArrowPosition.TOP, ArrowPosition.BOTTOM -> with(density) { arrowHeight.toPx() }
                        else -> 0f
                    }).coerceAtMost(with(density) { screenHeight.toPx() - verticalScreenPadding.toPx() * 2 })

                    val bubbleFullWidthDp = with(density) { constrainedBubbleFullWidthPx.toDp() }
                    val bubbleFullHeightDp = with(density) { constrainedBubbleFullHeightPx.toDp() }

                    val targetCenterX = targetComponentRect.center.x
                    val targetCenterYAdjusted = targetComponentRect.center.y - statusBarHeightPx

                    val targetWidthPx = targetComponentRect.width
                    val targetHeightPx = targetComponentRect.height

                    val arrowTargetOffsetPx = with(density) { arrowTargetOffset.toPx() }

                    val desiredBubbleX = when (arrowPosition) {
                        ArrowPosition.LEFT -> targetCenterX - (targetWidthPx / 2) - constrainedBubbleFullWidthPx + arrowTargetOffsetPx
                        ArrowPosition.RIGHT -> targetCenterX + (targetWidthPx / 2) + arrowTargetOffsetPx
                        ArrowPosition.TOP, ArrowPosition.BOTTOM -> targetCenterX - (constrainedBubbleFullWidthPx / 2)
                    }

                    val desiredBubbleY = when (arrowPosition) {
                        ArrowPosition.TOP -> targetCenterYAdjusted - (targetHeightPx / 2) - constrainedBubbleFullHeightPx + arrowTargetOffsetPx
                        ArrowPosition.BOTTOM -> targetCenterYAdjusted + (targetHeightPx / 2) + arrowTargetOffsetPx
                        ArrowPosition.LEFT, ArrowPosition.RIGHT -> targetCenterYAdjusted - (constrainedBubbleFullHeightPx / 2)
                    }

                    val minX = with(density) { horizontalScreenPadding.toPx() }
                    val maxX = with(density) { screenWidth.toPx() } - constrainedBubbleFullWidthPx - minX
                    val finalBubbleX = desiredBubbleX.coerceIn(minX, maxX)

                    val minY = with(density) { verticalScreenPadding.toPx() }
                    val maxY = with(density) { screenHeight.toPx() } - constrainedBubbleFullHeightPx - minY
                    val finalBubbleY = desiredBubbleY.coerceIn(minY, maxY)

                    val finalArrowOffsetPx = when (arrowPosition) {
                        ArrowPosition.LEFT, ArrowPosition.RIGHT -> targetCenterYAdjusted - finalBubbleY
                        ArrowPosition.TOP, ArrowPosition.BOTTOM -> targetCenterX - finalBubbleX
                    }
                    val finalArrowOffsetDp = with(density) { finalArrowOffsetPx.toDp() }

                    val bubbleCardPlaceable = subcompose("bubbleCard") {
                        val bubbleShape = remember(arrowPosition, finalArrowOffsetDp, arrowWidth, arrowHeight, cornerRadius) {
                            BubbleShape(
                                arrowPosition = arrowPosition,
                                arrowOffset = finalArrowOffsetDp,
                                arrowWidth = arrowWidth,
                                arrowHeight = arrowHeight,
                                cornerRadius = cornerRadius
                            )
                        }
                        Card(
                            shape = bubbleShape,
                            colors = CardDefaults.cardColors(containerColor = backgroundColor),
                            modifier = Modifier
                                .size(width = bubbleFullWidthDp, height = bubbleFullHeightDp)
                                .drawBehind {
                                    if (bubbleBorderWidth > 0.dp && bubbleBorderColor != Color.Transparent) {
                                        val outline = bubbleShape.createOutline(size, layoutDirection, this)
                                        if (outline is Outline.Generic) {
                                            drawPath(
                                                path = outline.path,
                                                color = bubbleBorderColor,
                                                style = Stroke(width = bubbleBorderWidth.toPx())
                                            )
                                            // Added code to mask the arrow base seam
                                            if (arrowHeight > 0.dp && arrowWidth > 0.dp) {
                                                val arrowHeightPx = arrowHeight.toPx()
                                                val arrowWidthPx = arrowWidth.toPx()
                                                val borderWidthPx = bubbleBorderWidth.toPx()
                                                val currentArrowOffsetPx = finalArrowOffsetDp.toPx()

                                                // Parameters for mask adjustment
                                                val maskExpansionPx = 1.dp.toPx()
                                                val maskShiftPx = 0.75.dp.toPx()
                                                val taperPx = 0.5.dp.toPx() // How much to taper the mask edge (remains 0.5.dp)

                                                var seamRect: Rect?
                                                // New half-thickness for the masking rectangle, including expansion
                                                val newMaskHalfThickness = (borderWidthPx / 2f) + maskExpansionPx

                                                when (arrowPosition) {
                                                    ArrowPosition.TOP -> {
                                                        val yCenterOriginal = size.height - arrowHeightPx
                                                        val yCenterShifted = yCenterOriginal - maskShiftPx
                                                        val xInitialStart = currentArrowOffsetPx - arrowWidthPx / 2
                                                        seamRect = Rect(
                                                            left = xInitialStart + borderWidthPx,
                                                            top = yCenterShifted - newMaskHalfThickness,
                                                            right = xInitialStart + arrowWidthPx - borderWidthPx,
                                                            bottom = yCenterShifted + newMaskHalfThickness
                                                        )
                                                    }

                                                    ArrowPosition.BOTTOM -> {
                                                        val yCenterOriginal = arrowHeightPx
                                                        val yCenterShifted = yCenterOriginal + maskShiftPx
                                                        val xInitialStart = currentArrowOffsetPx - arrowWidthPx / 2
                                                        seamRect = Rect(
                                                            left = xInitialStart + borderWidthPx,
                                                            top = yCenterShifted - newMaskHalfThickness,
                                                            right = xInitialStart + arrowWidthPx - borderWidthPx,
                                                            bottom = yCenterShifted + newMaskHalfThickness
                                                        )
                                                    }

                                                    ArrowPosition.LEFT -> {
                                                        val xCenterOriginal = size.width - arrowHeightPx
                                                        val xCenterShifted = xCenterOriginal - maskShiftPx
                                                        val yInitialStart = currentArrowOffsetPx - arrowWidthPx / 2
                                                        seamRect = Rect(
                                                            left = xCenterShifted - newMaskHalfThickness,
                                                            top = yInitialStart + borderWidthPx,
                                                            right = xCenterShifted + newMaskHalfThickness,
                                                            bottom = yInitialStart + arrowWidthPx - borderWidthPx
                                                        )
                                                    }

                                                    ArrowPosition.RIGHT -> {
                                                        val xCenterOriginal = arrowHeightPx
                                                        val xCenterShifted = xCenterOriginal + maskShiftPx
                                                        val yInitialStart = currentArrowOffsetPx - arrowWidthPx / 2
                                                        seamRect = Rect(
                                                            left = xCenterShifted - newMaskHalfThickness,
                                                            top = yInitialStart + borderWidthPx,
                                                            right = xCenterShifted + newMaskHalfThickness,
                                                            bottom = yInitialStart + arrowWidthPx - borderWidthPx
                                                        )
                                                    }
                                                }

                                                // Ensure the resulting rect has a positive width and height
                                                if (seamRect.width > 0f && seamRect.height > 0f) {
                                                    val maskPath = ComposePath()

                                                    when (arrowPosition) {
                                                        ArrowPosition.TOP -> {
                                                            if (seamRect.width > 2 * taperPx) {
                                                                maskPath.moveTo(seamRect.left, seamRect.bottom)
                                                                maskPath.lineTo(seamRect.right, seamRect.bottom)
                                                                maskPath.lineTo(seamRect.right - taperPx, seamRect.top)
                                                                maskPath.lineTo(seamRect.left + taperPx, seamRect.top)
                                                                maskPath.close()
                                                            } else {
                                                                maskPath.addRect(seamRect)
                                                            }
                                                        }

                                                        ArrowPosition.BOTTOM -> {
                                                            if (seamRect.width > 2 * taperPx) {
                                                                maskPath.moveTo(seamRect.left, seamRect.top)
                                                                maskPath.lineTo(seamRect.right, seamRect.top)
                                                                maskPath.lineTo(seamRect.right - taperPx, seamRect.bottom)
                                                                maskPath.lineTo(seamRect.left + taperPx, seamRect.bottom)
                                                                maskPath.close()
                                                            } else {
                                                                maskPath.addRect(seamRect)
                                                            }
                                                        }

                                                        ArrowPosition.LEFT -> {
                                                            if (seamRect.height > 2 * taperPx) {
                                                                maskPath.moveTo(seamRect.right, seamRect.top)
                                                                maskPath.lineTo(seamRect.right, seamRect.bottom)
                                                                maskPath.lineTo(seamRect.left, seamRect.bottom - taperPx)
                                                                maskPath.lineTo(seamRect.left, seamRect.top + taperPx)
                                                                maskPath.close()
                                                            } else {
                                                                maskPath.addRect(seamRect)
                                                            }
                                                        }

                                                        ArrowPosition.RIGHT -> {
                                                            if (seamRect.height > 2 * taperPx) {
                                                                maskPath.moveTo(seamRect.left, seamRect.top)
                                                                maskPath.lineTo(seamRect.left, seamRect.bottom)
                                                                maskPath.lineTo(seamRect.right, seamRect.bottom - taperPx)
                                                                maskPath.lineTo(seamRect.right, seamRect.top + taperPx)
                                                                maskPath.close()
                                                            } else {
                                                                maskPath.addRect(seamRect)
                                                            }
                                                        }
                                                    }

                                                    drawPath(
                                                        path = maskPath,
                                                        color = Color.Transparent,
                                                        blendMode = BlendMode.Clear
                                                    )

                                                }
                                            }
                                        }
                                    }
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(
                                        when (arrowPosition) {
                                            ArrowPosition.LEFT -> PaddingValues(start = cornerRadius, top = cornerRadius, end = arrowHeight + cornerRadius, bottom = cornerRadius)
                                            ArrowPosition.RIGHT -> PaddingValues(start = arrowHeight + cornerRadius, top = cornerRadius, end = cornerRadius, bottom = cornerRadius)
                                            ArrowPosition.TOP -> PaddingValues(start = cornerRadius, top = cornerRadius, end = cornerRadius, bottom = arrowHeight + cornerRadius)
                                            ArrowPosition.BOTTOM -> PaddingValues(start = cornerRadius, top = arrowHeight + cornerRadius, end = cornerRadius, bottom = cornerRadius)
                                        }
                                    )
                            ) {
                                content(onDismissRequest)
                            }
                        }
                    }.first().measure(
                        constraints.copy(
                            minWidth = constrainedBubbleFullWidthPx.roundToInt(),
                            minHeight = constrainedBubbleFullHeightPx.roundToInt(),
                            maxWidth = constrainedBubbleFullWidthPx.roundToInt(),
                            maxHeight = constrainedBubbleFullHeightPx.roundToInt()
                        )
                    )

                    layout(constraints.maxWidth, constraints.maxHeight) {
                        bubbleCardPlaceable.place(x = finalBubbleX.roundToInt(), y = finalBubbleY.roundToInt())
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.NEXUS_6, name = "Bubble Screen Preview")
@Composable
fun BubblePreviewScreen() {
    var targetRectTopLeft by remember { mutableStateOf<Rect?>(null) }
    var targetRectTopRight by remember { mutableStateOf<Rect?>(null) }
    var targetRectBottomLeft by remember { mutableStateOf<Rect?>(null) }
    var targetRectBottomRight by remember { mutableStateOf<Rect?>(null) }
    var targetRectCenter by remember { mutableStateOf<Rect?>(null) }

    var isCentralBubbleVisible by remember { mutableStateOf(true) }

    val commonSettings = remember {
        BubblesSettings(
            arrowWidth = DEFAULT_ARROW_WIDTH,
            arrowHeight = DEFAULT_ARROW_HEIGHT,
            cornerRadius = DEFAULT_CORNER_RADIUS,
            backgroundColor = DEFAULT_BACKGROUND_COLOR, // This will be overridden per bubble
            bubbleBorderColor = DEFAULT_BORDER_COLOR, // Default border color from settings
            bubbleBorderWidth = DEFAULT_BORDER_WIDTH, // Default border width from settings
            horizontalScreenPadding = DEFAULT_HORIZONTAL_SCREEN_PADDING,
            verticalScreenPadding = DEFAULT_VERTICAL_SCREEN_PADDING,
            scrimColor = Color(0x00000000),
            dismissOnScrimClick = true,
            enterAnimationDurationMs = 0,
            exitAnimationDurationMs = 0
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(100.dp)
                .background(Color(0xFF29B6F6))
                .onGloballyPositioned { coordinates ->
                    targetRectTopLeft = coordinates.boundsInWindow()
                },
            contentAlignment = Alignment.Center
        ) {
            Text("TopStart", color = Color.White)
        }

        Bubble(
            settings = commonSettings.copy(
                backgroundColor = Color(0xFF29B6F6).copy(alpha = 0.8f),
                bubbleBorderColor = Color.Black, // Example of border
                bubbleBorderWidth = 1.dp      // Example of border
            ),
            bubbleData = BubbleData(
                id = "TopStart",
                arrowPosition = ArrowPosition.BOTTOM,
                content = {
                    Text("Bubble TopStart", color = Color.White, modifier = Modifier.padding(8.dp))
                }
            ),
            targetComponentRect = targetRectTopLeft,
            isVisible = true,
            modifier = Modifier.heightIn(max = 100.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(100.dp)
                .background(Color(0xFFFFB74D))
                .onGloballyPositioned { coordinates ->
                    targetRectTopRight = coordinates.boundsInWindow()
                },
            contentAlignment = Alignment.Center
        ) {
            Text("TopEnd", color = Color.Black)
        }

        Bubble(
            settings = commonSettings.copy(
                backgroundColor = Color(0xFFFFB74D).copy(alpha = 0.8f),
                bubbleBorderColor = Color.Black, // Example of border
                bubbleBorderWidth = 1.dp      // Example of border
            ),
            bubbleData = BubbleData(
                id = "TopEnd",
                arrowPosition = ArrowPosition.LEFT,
                content = {
                    Text("Bubble TopEnd", color = Color.Black, modifier = Modifier.padding(8.dp))
                }
            ),
            targetComponentRect = targetRectTopRight,
            isVisible = true,
            modifier = Modifier.heightIn(max = 100.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .size(100.dp)
                .background(Color(0xFFFFA726))
                .onGloballyPositioned { coordinates ->
                    targetRectBottomLeft = coordinates.boundsInWindow()
                },
            contentAlignment = Alignment.Center
        ) {
            Text("BottomStart", color = Color.Black)
        }

        Bubble(
            settings = commonSettings.copy(
                backgroundColor = Color(0xFFFFA726).copy(alpha = 0.8f),
                bubbleBorderColor = Color.Black, // Example of border
                bubbleBorderWidth = 1.dp      // Example of border
            ),
            bubbleData = BubbleData(
                id = "bubble3",
                arrowPosition = ArrowPosition.RIGHT,
                content = {
                    Text("Bubble BottomStart", color = Color.Black, modifier = Modifier.padding(8.dp))
                }
            ),
            targetComponentRect = targetRectBottomLeft,
            isVisible = true,
            modifier = Modifier.heightIn(max = 100.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(100.dp)
                .background(Color(0xFF00ACC1))
                .onGloballyPositioned { coordinates ->
                    targetRectBottomRight = coordinates.boundsInWindow()
                },
            contentAlignment = Alignment.Center
        ) {
            Text("BottomEnd", color = Color.White)
        }

        Bubble(
            settings = commonSettings.copy(
                backgroundColor = Color(0xFF00ACC1).copy(alpha = 0.8f),
                bubbleBorderColor = Color.Black, // Example of border
                bubbleBorderWidth = 1.dp      // Example of border
            ),
            bubbleData = BubbleData(
                id = "bubble4",
                arrowPosition = ArrowPosition.TOP,
                content = {
                    Text("Bubble BottomEnd", color = Color.White, modifier = Modifier.padding(8.dp))
                }
            ),
            targetComponentRect = targetRectBottomRight,
            isVisible = true,
            modifier = Modifier.heightIn(max = 100.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(100.dp)
                .background(Color(0xFF0277BD))
                .onGloballyPositioned { coordinates ->
                    targetRectCenter = coordinates.boundsInWindow()
                },
            contentAlignment = Alignment.Center
        ) {
            Text("Center", color = Color.White)
        }

        Bubble(
            settings = commonSettings.copy(
                backgroundColor = Color(0xFF0277BD).copy(alpha = 0.8f),
                bubbleBorderColor = Color(0xFF025C91),
                bubbleBorderWidth = 2.dp
            ),
            bubbleData = BubbleData(
                id = "bubbleCenter",
                arrowPosition = ArrowPosition.BOTTOM,
                content = {
                    Text("Bubble Center with Red Border", color = Color.White, modifier = Modifier.padding(8.dp))
                }
            ),
            targetComponentRect = targetRectCenter,
            onDismissRequest = { isCentralBubbleVisible = false },
            isVisible = isCentralBubbleVisible,
            modifier = Modifier.heightIn(max = 100.dp)
        )
    }
}

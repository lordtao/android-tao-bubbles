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
import androidx.compose.foundation.layout.navigationBars
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
 * A [Shape] implementation for drawing a bubble with an arrow.
 * The shape consists of a rectangular body with rounded corners and a triangular arrow
 * attached to one of its sides.
 *
 * @param arrowPosition Specifies which side of the bubble body the arrow will be on ([ArrowPosition.LEFT], [ArrowPosition.RIGHT], [ArrowPosition.TOP], [ArrowPosition.BOTTOM]).
 * @param arrowOffset The offset of the arrow's base center from the center of the corresponding side of the bubble body.
 *                    For [ArrowPosition.TOP] and [ArrowPosition.BOTTOM], this is a horizontal offset.
 *                    For [ArrowPosition.LEFT] and [ArrowPosition.RIGHT], this is a vertical offset.
 * @param arrowWidth The width of the arrow's base.
 * @param arrowHeight The length (height) of the arrow from its base to its tip.
 * @param cornerRadius The corner radius for the bubble body.
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

/**
 * Displays a Bubble managed by a [BubbleShowController].
 * This overload simplifies showing bubbles in a sequence or controlled manner,
 * deriving most settings and state from the controller.
 *
 * @param modifier Modifier for this composable.
 * @param controller The [BubbleShowController] managing this bubble's display and behavior.
 * @param bubbleData The [BubbleData] containing specific content and arrow position for this bubble.
 * @param isVisible Controls the visibility of the bubble. Typically managed by the controller.
 */
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
        onStopShowRequest = { controller.stopShow() }, // Added
        arrowTargetOffset = controller.settings.arrowTargetOffset,
        enterAnimationDurationMs = controller.settings.enterAnimationDurationMs,
        exitAnimationDurationMs = controller.settings.exitAnimationDurationMs,
        isVisible = isVisible,
        content = bubbleData.content
    )
}

/**
 * Displays a Bubble with explicit settings and target.
 * This overload provides fine-grained control over a single bubble's appearance and behavior
 * when not using a [BubbleShowController].
 *
 * @param modifier Modifier for this composable.
 * @param settings The [BubblesSettings] to configure the bubble's appearance and animations.
 * @param bubbleData The [BubbleData] containing the unique ID, content, and arrow position.
 * @param targetComponentRect The [Rect] of the target component this bubble is pointing to. If null, the bubble is not shown.
 * @param onDismissRequest Callback invoked when the bubble requests to be dismissed.
 * @param onStopShowRequest Callback invoked when a request to stop an entire bubble sequence is made (if applicable).
 * @param isVisible Controls the visibility of the bubble.
 */
@Composable
fun Bubble(
    modifier: Modifier = Modifier,
    settings: BubblesSettings = BubblesSettings(),
    bubbleData: BubbleData,
    targetComponentRect: Rect?,
    onDismissRequest: () -> Unit = {},
    onStopShowRequest: () -> Unit = {},
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
        onStopShowRequest = onStopShowRequest, // Passed down
        arrowTargetOffset = settings.arrowTargetOffset,
        enterAnimationDurationMs = settings.enterAnimationDurationMs,
        exitAnimationDurationMs = settings.exitAnimationDurationMs,
        isVisible = isVisible,
        content = bubbleData.content
    )
}

/**
 * A composable that displays a customizable bubble, pointing to a target UI element.
 * It includes a scrim background, entrance/exit animations, and positioning logic
 * to place the bubble relative to a [targetComponentRect].
 *
 * The bubble's appearance and behavior are highly configurable.
 *
 * @param modifier Modifier for this composable.
 * @param id A unique identifier for the bubble, used for transition state.
 * @param targetComponentRect The [Rect] of the target component this bubble is pointing to. If null, the bubble is not shown.
 * @param arrowPosition The side of the bubble where the arrow should appear.
 * @param arrowWidth The width of the arrow's base.
 * @param arrowHeight The height (length) of the arrow.
 * @param cornerRadius The corner radius of the bubble's body.
 * @param backgroundColor The background color of the bubble.
 * @param bubbleBorderColor The color of the bubble's border.
 * @param bubbleBorderWidth The width of the bubble's border.
 * @param horizontalScreenPadding Horizontal padding from the screen edges.
 * @param verticalScreenPadding Vertical padding from the screen edges.
 * @param scrimColor The color of the scrim background displayed behind the bubble.
 * @param dismissOnScrimClick If true, the bubble will be dismissed when the scrim is clicked.
 * @param onDismissRequest Callback invoked when the bubble requests to be dismissed (e.g., scrim click, or action within content).
 * @param onStopShowRequest Callback invoked when a request to stop the entire bubble sequence is made (e.g., by an action within content).
 * @param arrowTargetOffset An offset applied to the arrow's position along the axis of the target component's side.
 *                          For example, for [ArrowPosition.LEFT], this will shift the bubble vertically along the left edge of the target.
 * @param enterAnimationDurationMs Duration of the enter animation in milliseconds.
 * @param exitAnimationDurationMs Duration of the exit animation in milliseconds.
 * @param isVisible Controls the visibility of the bubble. Triggers enter/exit animations.
 * @param content The composable content to be displayed inside the bubble. It receives two lambda functions:
 *                `onDismissClick` (which is `onDismissRequest`) and `onStopShowRequest`
 *                that can be used to dismiss the current bubble or stop the entire bubble sequence, respectively.
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
    onStopShowRequest: () -> Unit = {},
    arrowTargetOffset: Dp = 0.dp,
    enterAnimationDurationMs: Int = DEFAULT_ANIMATION_DURATION_MS,
    exitAnimationDurationMs: Int = DEFAULT_ANIMATION_DURATION_MS,
    isVisible: Boolean = true,
    content: @Composable (onDismissClick: () -> Unit, onStopShowRequest: () -> Unit) -> Unit,
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
                    onDismissRequest() // This dismisses the current bubble or triggers showNext via controller
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
                val navigationBarsHeightPx = WindowInsets.navigationBars.getBottom(density)

                // SubcomposeLayout for measuring and positioning the bubble itself
                SubcomposeLayout(modifier = Modifier.fillMaxSize()) { constraints ->
                    // density, windowInfo, screenWidth (Dp), screenHeight (Dp) уже определены
                    // statusBarHeightPx, navigationBarsHeightPx определены

                    val cornerRadiusPx = with(density) { cornerRadius.toPx() }
                    val arrowLengthPx = with(density) { arrowHeight.toPx() } // Dp значение arrowHeight используется как длина стрелки

                    // Максимально доступная область для пузыря на экране, после учета экранных отступов
                    val hScreenPaddingPx = with(density) { horizontalScreenPadding.toPx() }
                    val vScreenPaddingPx = with(density) { verticalScreenPadding.toPx() }

                    // constraints.maxWidth это фактически ширина устройства, constraints.maxHeight - высота устройства
                    val maxBubbleAreaOnScreenWidthPx = constraints.maxWidth - 2 * hScreenPaddingPx
                    val maxBubbleAreaOnScreenHeightPx = constraints.maxHeight - 2 * vScreenPaddingPx

                    // Максимальная ширина, которую может занять *весь пузырь*, учитывая целевой компонент для LEFT/RIGHT
                    val finalMaxBubbleFullWidthPx = if (arrowPosition == ArrowPosition.LEFT || arrowPosition == ArrowPosition.RIGHT) {
                        (maxBubbleAreaOnScreenWidthPx - targetComponentRect.width).coerceAtLeast(0f)
                    } else {
                        maxBubbleAreaOnScreenWidthPx
                    }
                    // Максимальная высота, которую может занять *весь пузырь*
                    val finalMaxBubbleFullHeightPx = maxBubbleAreaOnScreenHeightPx

                    // Определяем внутренние отступы в границах пузыря, занимаемые не-контентными элементами (углы, тело стрелки)
                    // Это используется для определения пространства для самого контента.
                    val horizontalPaddingInsideBubbleForContentCalc = when (arrowPosition) {
                        ArrowPosition.LEFT, ArrowPosition.RIGHT -> 2 * cornerRadiusPx + arrowLengthPx
                        else -> 2 * cornerRadiusPx // Стрелка сверху/снизу вертикальна, не занимает горизонтальное пространство у тела
                    }
                    val verticalPaddingInsideBubbleForContentCalc = when (arrowPosition) {
                        ArrowPosition.TOP, ArrowPosition.BOTTOM -> 2 * cornerRadiusPx + arrowLengthPx
                        else -> 2 * cornerRadiusPx // Стрелка слева/справа горизонтальна
                    }

                    // Максимальные размеры для самого контента
                    val contentMeasureMaxWidthPx = (finalMaxBubbleFullWidthPx - horizontalPaddingInsideBubbleForContentCalc).coerceAtLeast(0f)
                    val contentMeasureMaxHeightPx = (finalMaxBubbleFullHeightPx - verticalPaddingInsideBubbleForContentCalc).coerceAtLeast(0f)

                    val contentMeasurable = subcompose("bubbleContent") { content(onDismissRequest, onStopShowRequest) }.first()
                    val contentPlaceable = contentMeasurable.measure(
                        Constraints(
                            minWidth = 0,
                            minHeight = 0,
                            maxWidth = contentMeasureMaxWidthPx.roundToInt(),
                            maxHeight = contentMeasureMaxHeightPx.roundToInt()
                        )
                    )

                    val measuredContentWidthPx = contentPlaceable.width.toFloat()
                    val measuredContentHeightPx = contentPlaceable.height.toFloat()

                    // Фактический размер тела пузыря (контент + 2*радиус скругления)
                    val bubbleBodyWidthPx = measuredContentWidthPx + 2 * cornerRadiusPx
                    val bubbleBodyHeightPx = measuredContentHeightPx + 2 * cornerRadiusPx

                    // Рассчитываем финальные размеры пузыря, убеждаясь, что они не превышают вычисленные максимумы
                    val actualBubbleFullWidthPx = (when (arrowPosition) {
                        ArrowPosition.LEFT, ArrowPosition.RIGHT -> bubbleBodyWidthPx + arrowLengthPx
                        else -> bubbleBodyWidthPx // Для TOP/BOTTOM, длина стрелки вертикальна
                    }).coerceAtMost(finalMaxBubbleFullWidthPx)

                    val actualBubbleFullHeightPx = (when (arrowPosition) {
                        ArrowPosition.TOP, ArrowPosition.BOTTOM -> bubbleBodyHeightPx + arrowLengthPx
                        else -> bubbleBodyHeightPx // Для LEFT/RIGHT, длина стрелки горизонтальна
                    }).coerceAtMost(finalMaxBubbleFullHeightPx)

                    // Сохраняем старые имена переменных для совместимости с остальным кодом,
                    // если они используются для создания Dp версий.
                    val constrainedBubbleFullWidthPx = actualBubbleFullWidthPx
                    val constrainedBubbleFullHeightPx = actualBubbleFullHeightPx

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

                    val minX = with(density) { horizontalScreenPadding.toPx() } //+ shiftLeftX
                    val maxX = with(density) { screenWidth.toPx() } - constrainedBubbleFullWidthPx - minX// - shiftRightX
                    val finalBubbleX = desiredBubbleX.coerceIn(minX, maxX)

                    val minY = with(density) { verticalScreenPadding.toPx() }
                    val maxY = with(density) { screenHeight.toPx() } - constrainedBubbleFullHeightPx - navigationBarsHeightPx - statusBarHeightPx - minY
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
                                content(onDismissRequest, onStopShowRequest) // Modified call
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
                content = { onDismissClick, onStopShowRequest ->
                    Text(
                        "Bubble TopStart", color = Color.White, modifier = Modifier
                            .padding(8.dp)
                            .clickable { onDismissClick() })
                }
            ),
            targetComponentRect = targetRectTopLeft,
            isVisible = true,
            modifier = Modifier.heightIn(max = 100.dp)
            // onStopShowRequestCallback will use its default {} for previews if not explicitly passed
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
                content = { onDismissClick, onStopShowRequest ->
                    Text(
                        "Bubble TopEnd", color = Color.Black, modifier = Modifier
                            .padding(8.dp)
                            .clickable { onDismissClick() })
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
                content = { onDismissClick, onStopShowRequest ->
                    Text(
                        "Bubble BottomStart", color = Color.Black, modifier = Modifier
                            .padding(8.dp)
                            .clickable { onDismissClick() })
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
                content = { onDismissClick, onStopShowRequest ->
                    Text(
                        "Bubble BottomEnd", color = Color.White, modifier = Modifier
                            .padding(8.dp)
                            .clickable { onDismissClick() })
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
                bubbleBorderColor = Color(0xFF025C91), // Example of border
                bubbleBorderWidth = 2.dp      // Example of border
            ),
            bubbleData = BubbleData(
                id = "bubbleCenter",
                arrowPosition = ArrowPosition.BOTTOM,
                content = { onDismissClick, onStopShowRequest -> // Already correct in preview
                    Text(
                        "Bubble Center with Border", color = Color.White, modifier = Modifier
                            .padding(8.dp)
                            .clickable { onDismissClick() })
                }
            ),
            targetComponentRect = targetRectCenter,
            onDismissRequest = { isCentralBubbleVisible = false },
            isVisible = isCentralBubbleVisible,
            modifier = Modifier.heightIn(max = 100.dp)
        )
    }
}

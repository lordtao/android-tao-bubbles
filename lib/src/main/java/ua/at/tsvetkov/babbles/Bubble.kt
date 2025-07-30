package ua.at.tsvetkov.babbles

/**
 * Created by Alexandr Tsvetkov on 28.07.2025.
 */
import android.graphics.Path
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button as AndroidButton // Алиас для избежания конфликта с Compose Button
import android.widget.TextView
import androidx.compose.animation.AnimatedVisibility // Импорт для анимации
import androidx.compose.animation.fadeIn // Импорт для анимации
import androidx.compose.animation.fadeOut // Импорт для анимации
import androidx.compose.animation.scaleIn // Импорт для анимации
import androidx.compose.animation.scaleOut // Импорт для анимации
import androidx.compose.animation.core.tween // Импорт для настройки продолжительности анимации
import androidx.compose.animation.EnterTransition // Импорт для отключения анимации
import androidx.compose.animation.ExitTransition // Импорт для отключения анимации
import androidx.compose.animation.core.MutableTransitionState // Импорт для управления состоянием анимации
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults // Импортируем CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect // Используем этот Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import androidx.compose.ui.tooling.preview.Devices // Импорт Devices
import android.content.res.Configuration // Импорт Configuration
import androidx.compose.ui.unit.Constraints // Импорт Constraints
import androidx.compose.foundation.clickable // Импорт clickable

// Enum для позиции стрелки (хвостика)
enum class ArrowPosition {
    LEFT, RIGHT, TOP, BOTTOM
}

// Пользовательская форма для пузыря с хвостиком
class BubbleShape(
    private val arrowPosition: ArrowPosition,
    private val arrowOffset: Dp, // Отступ стрелки от края пузыря (для центрирования)
    private val arrowWidth: Dp,
    private val arrowHeight: Dp,
    private val cornerRadius: Dp
) : Shape {
    override fun createOutline(size: Size, layoutDirection: androidx.compose.ui.unit.LayoutDirection, density: Density): Outline {
        val path = Path()

        val arrowWidthPx = with(density) { arrowWidth.toPx() }
        val arrowHeightPx = with(density) { arrowHeight.toPx() }
        val arrowOffsetPx = with(density) { arrowOffset.toPx() }
        val cornerRadiusPx = with(density) { cornerRadius.toPx() }

        // Размеры тела пузыря (прямоугольная часть, без учета стрелки)
        val bubbleRectLeft: Float
        val bubbleRectTop: Float
        val bubbleRectRight: Float
        val bubbleRectBottom: Float

        when (arrowPosition) {
            ArrowPosition.LEFT -> { // Пузырь слева от цели, стрелка указывает ВПРАВО (на правой стороне пузыря)
                bubbleRectLeft = 0f
                bubbleRectTop = 0f
                bubbleRectRight = size.width - arrowHeightPx
                bubbleRectBottom = size.height
            }
            ArrowPosition.RIGHT -> { // Пузырь справа от цели, стрелка указывает ВЛЕВО (на левой стороне пузыря)
                bubbleRectLeft = arrowHeightPx
                bubbleRectTop = 0f
                bubbleRectRight = size.width
                bubbleRectBottom = size.height
            }
            ArrowPosition.TOP -> { // Пузырь над целью, стрелка указывает ВНИЗ (на нижней стороне пузыря)
                bubbleRectLeft = 0f
                bubbleRectTop = 0f
                bubbleRectRight = size.width
                bubbleRectBottom = size.height - arrowHeightPx
            }
            ArrowPosition.BOTTOM -> { // Пузырь под целью, стрелка указывает ВВЕРХ (на верхней стороне пузыря)
                bubbleRectLeft = 0f
                bubbleRectTop = arrowHeightPx
                bubbleRectRight = size.width
                bubbleRectBottom = size.height
            }
        }

        // Рисуем тело пузыря (прямоугольник с закругленными углами)
        path.addRoundRect(
            bubbleRectLeft, bubbleRectTop, bubbleRectRight, bubbleRectBottom,
            cornerRadiusPx, cornerRadiusPx, Path.Direction.CW
        )

        // Рисуем стрелку
        when (arrowPosition) {
            ArrowPosition.LEFT -> { // Стрелка указывает ВПРАВО (на правой стороне пузыря)
                path.moveTo(bubbleRectRight, arrowOffsetPx - arrowWidthPx / 2) // Верхняя точка основания стрелки
                path.lineTo(bubbleRectRight, arrowOffsetPx + arrowWidthPx / 2) // Нижняя точка основания стрелки
                path.lineTo(size.width, arrowOffsetPx) // Кончик стрелки (на правом краю общей формы)
                path.close()
            }
            ArrowPosition.RIGHT -> { // Стрелка указывает ВЛЕВО (на левой стороне пузыря)
                path.moveTo(bubbleRectLeft, arrowOffsetPx + arrowWidthPx / 2) // Нижняя точка основания стрелки (на левом краю тела пузыря)
                path.lineTo(bubbleRectLeft, arrowOffsetPx - arrowWidthPx / 2) // Верхняя точка основания стрелки
                path.lineTo(0f, arrowOffsetPx) // Кончик стрелки (на левом краю общей формы)
                path.close()
            }
            ArrowPosition.TOP -> { // Стрелка указывает ВНИЗ (на нижней стороне пузыря)
                path.moveTo(arrowOffsetPx - arrowWidthPx / 2, bubbleRectBottom) // Левая точка основания стрелки
                path.lineTo(arrowOffsetPx + arrowWidthPx / 2, bubbleRectBottom) // Правая точка основания стрелки
                path.lineTo(arrowOffsetPx, size.height) // Кончик стрелки (на нижнем краю общей формы)
                path.close()
            }
            ArrowPosition.BOTTOM -> { // Стрелка указывает ВВЕРХ (на верхней стороне пузыря)
                path.moveTo(arrowOffsetPx + arrowWidthPx / 2, bubbleRectTop) // Правая точка основания стрелки (на верхнем краю тела пузыря)
                path.lineTo(arrowOffsetPx - arrowWidthPx / 2, bubbleRectTop) // Левая точка основания стрелки
                path.lineTo(arrowOffsetPx, 0f) // Кончик стрелки (на верхнем краю общей формы)
                path.close()
            }
        }
        return Outline.Generic(path.asComposePath())
    }
}

// Константы для общих настроек Bubble
private val DEFAULT_ARROW_WIDTH = 20.dp
private val DEFAULT_ARROW_HEIGHT = 10.dp
private val DEFAULT_CORNER_RADIUS = 8.dp
private val DEFAULT_BACKGROUND_COLOR = Color(0xFF424242)
private val DEFAULT_HORIZONTAL_SCREEN_PADDING = 16.dp // Установлено в 16.dp
private val DEFAULT_VERTICAL_SCREEN_PADDING = 16.dp   // Установлено в 16.dp
private val DEFAULT_SCRIM_COLOR = Color(0x52000000)
private val DEFAULT_ANIMATION_DURATION_MS = 300 // Продолжительность анимации по умолчанию

/**
 * Объект данных, содержащий общие настройки внешнего вида и поведения Bubble.
 */
data class BubbleCommonSettings(
    val arrowWidth: Dp = DEFAULT_ARROW_WIDTH,
    val arrowHeight: Dp = DEFAULT_ARROW_HEIGHT,
    val cornerRadius: Dp = DEFAULT_CORNER_RADIUS,
    val backgroundColor: Color = DEFAULT_BACKGROUND_COLOR,
    val horizontalScreenPadding: Dp = DEFAULT_HORIZONTAL_SCREEN_PADDING,
    val verticalScreenPadding: Dp = DEFAULT_VERTICAL_SCREEN_PADDING,
    val scrimColor: Color = DEFAULT_SCRIM_COLOR,
    val dismissOnScrimClick: Boolean = false,
    val enterAnimationDurationMs: Int = DEFAULT_ANIMATION_DURATION_MS, // Продолжительность анимации появления
    val exitAnimationDurationMs: Int = DEFAULT_ANIMATION_DURATION_MS   // Продолжительность анимации исчезновения
)

/**
 * Объект данных, содержащий специфичные для каждого экземпляра Bubble данные и поведение.
 */
data class BubbleData(
    val targetRect: Rect?,
    val arrowPosition: ArrowPosition,
    val isVisible: Boolean = true,
    val onDismissRequest: () -> Unit = {},
    val arrowTargetOffset: Dp = 0.dp,
    val content: @Composable () -> Unit
)

/**
 * Компонент Bubble с хвостиком, позиционируемый относительно целевого Composable.
 * Перегруженная функция, принимающая объекты BubbleCommonSettings и BubbleData.
 */
@Composable
fun Bubble(
    commonSettings: BubbleCommonSettings,
    bubbleData: BubbleData,
    modifier: Modifier = Modifier
) {
    Bubble(
        targetRect = bubbleData.targetRect,
        arrowPosition = bubbleData.arrowPosition,
        modifier = modifier,
        arrowWidth = commonSettings.arrowWidth,
        arrowHeight = commonSettings.arrowHeight,
        cornerRadius = commonSettings.cornerRadius,
        backgroundColor = commonSettings.backgroundColor,
        horizontalScreenPadding = commonSettings.horizontalScreenPadding,
        verticalScreenPadding = commonSettings.verticalScreenPadding,
        isVisible = bubbleData.isVisible, // isVisible теперь передается напрямую
        scrimColor = commonSettings.scrimColor,
        dismissOnScrimClick = commonSettings.dismissOnScrimClick,
        onDismissRequest = bubbleData.onDismissRequest,
        arrowTargetOffset = bubbleData.arrowTargetOffset,
        enterAnimationDurationMs = commonSettings.enterAnimationDurationMs, // Передаем настройки анимации
        exitAnimationDurationMs = commonSettings.exitAnimationDurationMs,   // Передаем настройки анимации
        content = bubbleData.content
    )
}

/**
 * Компонент Bubble с хвостиком, позиционируемый относительно целевого Composable.
 *
 * @param targetRect Прямоугольник, представляющий границы целевого элемента в оконных координатах.
 * @param arrowPosition Позиция хвостика Bubble (LEFT, RIGHT, TOP, BOTTOM) относительно целевого элемента.
 * @param modifier Модификатор для Bubble.
 * @param arrowWidth Ширина основания хвостика.
 * @param arrowHeight Длина хвостика.
 * @param cornerRadius Радиус закругления углов Bubble.
 * @param backgroundColor Цвет фона Bubble.
 * @param content Содержимое Bubble.
 * @param horizontalScreenPadding Отступ от левой и правой границ экрана.
 * @param verticalScreenPadding Отступ от верхней и нижней границ экрана.
 * @param isVisible Управляет видимостью пузыря.
 * @param scrimColor Цвет полупрозрачного фона под пузырем (включает прозрачность).
 * @param dismissOnScrimClick Если true, нажатие на полупрозрачный фон скроет пузырь.
 * @param onDismissRequest Колбэк, вызываемый при запросе на скрытие пузыря.
 * @param arrowTargetOffset Смещение стрелки относительно центра целевого компонента.
 * Положительное значение смещает стрелку от целевого компонента,
 * отрицательное - к нему.
 * @param enterAnimationDurationMs Продолжительность анимации появления пузыря.
 * @param exitAnimationDurationMs Продолжительность анимации исчезновения пузыря.
 */
@Composable
fun Bubble(
    targetRect: Rect?,
    arrowPosition: ArrowPosition,
    modifier: Modifier = Modifier,
    arrowWidth: Dp = DEFAULT_ARROW_WIDTH, // Используем константу
    arrowHeight: Dp = DEFAULT_ARROW_HEIGHT, // Используем константу
    cornerRadius: Dp = DEFAULT_CORNER_RADIUS, // Используем константу
    backgroundColor: Color = DEFAULT_BACKGROUND_COLOR, // Используем константу
    horizontalScreenPadding: Dp = DEFAULT_HORIZONTAL_SCREEN_PADDING, // Используем константу
    verticalScreenPadding: Dp = DEFAULT_VERTICAL_SCREEN_PADDING,   // Используем константу
    isVisible: Boolean = true,
    scrimColor: Color = DEFAULT_SCRIM_COLOR, // Используем константу
    dismissOnScrimClick: Boolean = false,
    onDismissRequest: () -> Unit = {},
    arrowTargetOffset: Dp = 0.dp,
    enterAnimationDurationMs: Int = DEFAULT_ANIMATION_DURATION_MS,
    exitAnimationDurationMs: Int = DEFAULT_ANIMATION_DURATION_MS,
    content: @Composable () -> Unit
) {
    // Если нет целевого прямоугольника, ничего не отображаем
    if (targetRect == null) return

    // Состояние для управления видимостью пузыря и его анимацией
    val bubbleTransitionState = remember { MutableTransitionState(false) }
    bubbleTransitionState.targetState = isVisible // Целевое состояние видимости пузыря

    // Scrim (полупрозрачный фон) будет отображаться, пока пузырь виден или анимируется
    val showScrim = bubbleTransitionState.currentState || bubbleTransitionState.targetState

    if (showScrim) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(scrimColor) // Используем scrimColor напрямую
                .clickable(enabled = dismissOnScrimClick) {
                    onDismissRequest()
                }
        ) {
            // AnimatedVisibility теперь применяется только к содержимому пузыря
            AnimatedVisibility(
                visibleState = bubbleTransitionState, // Используем общее состояние анимации
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
                val screenWidth = LocalConfiguration.current.screenWidthDp.dp
                val screenHeight = LocalConfiguration.current.screenHeightDp.dp

                // SubcomposeLayout для измерения и размещения самого пузыря
                SubcomposeLayout(modifier = Modifier.fillMaxSize()) { constraints ->
                    // 1. Измеряем содержимое Bubble, чтобы определить его размеры
                    // Вычисляем доступное пространство для контента, учитывая радиус углов и высоту стрелки
                    val horizontalContentPaddingPx = with(density) { cornerRadius.toPx() * 2 + if (arrowPosition == ArrowPosition.LEFT || arrowPosition == ArrowPosition.RIGHT) arrowHeight.toPx() else 0f }
                    val verticalContentPaddingPx = with(density) { cornerRadius.toPx() * 2 + if (arrowPosition == ArrowPosition.TOP || arrowPosition == ArrowPosition.BOTTOM) arrowHeight.toPx() else 0f }

                    val contentMeasurable = subcompose("bubbleContent") { content() }.first()
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

                    // 2. Вычисляем размеры тела пузыря (прямоугольная часть, включая отступы)
                    val bubbleBodyWidthPx = (measuredContentWidthPx + with(density) { cornerRadius.toPx() * 2 })
                    val bubbleBodyHeightPx = (measuredContentHeightPx + with(density) { cornerRadius.toPx() * 2 })

                    // 3. Вычисляем полные размеры Bubble, включая стрелку, и ограничиваем их экраном
                    val constrainedBubbleFullWidthPx = (bubbleBodyWidthPx + when (arrowPosition) {
                        ArrowPosition.LEFT, ArrowPosition.RIGHT -> with(density) { arrowHeight.toPx() }
                        else -> 0f
                    }).coerceAtMost(with(density) { screenWidth.toPx() - horizontalScreenPadding.toPx() * 2 }) // Ограничиваем общую ширину экраном с учетом отступов

                    val constrainedBubbleFullHeightPx = (bubbleBodyHeightPx + when (arrowPosition) {
                        ArrowPosition.TOP, ArrowPosition.BOTTOM -> with(density) { arrowHeight.toPx() }
                        else -> 0f
                    }).coerceAtMost(with(density) { screenHeight.toPx() - verticalScreenPadding.toPx() * 2 }) // Ограничиваем общую высоту экраном с учетом отступов

                    val bubbleFullWidthDp = with(density) { constrainedBubbleFullWidthPx.toDp() }
                    val bubbleFullHeightDp = with(density) { constrainedBubbleFullHeightPx.toDp() }

                    // 4. Получаем центр целевого элемента в оконных координатах
                    val targetCenterX = targetRect.center.x
                    val targetCenterY = targetRect.center.y
                    val targetWidthPx = targetRect.width
                    val targetHeightPx = targetRect.height

                    // 5. Вычисляем желаемые координаты верхнего левого угла Bubble в оконных координатах
                    val arrowTargetOffsetPx = with(density) { arrowTargetOffset.toPx() }

                    val desiredBubbleX = when (arrowPosition) {
                        ArrowPosition.LEFT -> targetCenterX - (targetWidthPx / 2) - constrainedBubbleFullWidthPx + arrowTargetOffsetPx
                        ArrowPosition.RIGHT -> targetCenterX + (targetWidthPx / 2) + arrowTargetOffsetPx
                        ArrowPosition.TOP, ArrowPosition.BOTTOM -> targetCenterX - (constrainedBubbleFullWidthPx / 2)
                    }

                    val desiredBubbleY = when (arrowPosition) {
                        ArrowPosition.TOP -> targetCenterY - (targetHeightPx / 2) - constrainedBubbleFullHeightPx + arrowTargetOffsetPx
                        ArrowPosition.BOTTOM -> targetCenterY + (targetHeightPx / 2) + arrowTargetOffsetPx
                        ArrowPosition.LEFT, ArrowPosition.RIGHT -> targetCenterY - (constrainedBubbleFullHeightPx / 2)
                    }

                    // 6. Ограничиваем позицию Bubble пределами экрана с учетом screenPadding
                    val minX = with(density) { horizontalScreenPadding.toPx() }
                    val maxX = with(density) { screenWidth.toPx() } - constrainedBubbleFullWidthPx - minX
                    val finalBubbleX = desiredBubbleX.coerceIn(minX, maxX)

                    val minY = with(density) { verticalScreenPadding.toPx() }
                    val maxY = with(density) { screenHeight.toPx() } - constrainedBubbleFullHeightPx - minY
                    val finalBubbleY = desiredBubbleY.coerceIn(minY, maxY)

                    // 7. Вычисляем смещение стрелки от края Bubble (для центрирования на цели)
                    val finalArrowOffsetPx = when (arrowPosition) {
                        ArrowPosition.LEFT, ArrowPosition.RIGHT -> targetCenterY - finalBubbleY
                        ArrowPosition.TOP, ArrowPosition.BOTTOM -> targetCenterX - finalBubbleX
                    }
                    val finalArrowOffsetDp = with(density) { finalArrowOffsetPx.toDp() }

                    // 8. Измеряем и размещаем сам Bubble (Card с пользовательской формой)
                    val bubbleCardPlaceable = subcompose("bubbleCard") {
                        Card(
                            shape = BubbleShape(
                                arrowPosition = arrowPosition,
                                arrowOffset = finalArrowOffsetDp, // Используем finalArrowOffsetDp
                                arrowWidth = arrowWidth,
                                arrowHeight = arrowHeight,
                                cornerRadius = cornerRadius
                            ),
                            colors = CardDefaults.cardColors(containerColor = backgroundColor),
                            modifier = Modifier
                                .size(width = bubbleFullWidthDp, height = bubbleFullHeightDp)
                        ) {
                            // Применяем внутренние отступы, чтобы содержимое не пересекалось с хвостиком
                            Box(modifier = Modifier
                                .padding(when(arrowPosition){
                                    // ArrowPosition.LEFT: Пузырь слева от цели, стрелка указывает ВПРАВО (стрелка на ПРАВОЙ стороне пузыря)
                                    ArrowPosition.LEFT -> PaddingValues(start = cornerRadius, top = cornerRadius, end = arrowHeight + cornerRadius, bottom = cornerRadius)
                                    // ArrowPosition.RIGHT: Пузырь справа от цели, стрелка указывает ВЛЕВО (стрелка на ЛЕВОЙ стороне пузыря)
                                    ArrowPosition.RIGHT -> PaddingValues(start = arrowHeight + cornerRadius, top = cornerRadius, end = cornerRadius, bottom = cornerRadius)
                                    // ArrowPosition.TOP: Пузырь над целью, стрелка указывает ВНИЗ (стрелка на НИЖНЕЙ стороне пузыря)
                                    ArrowPosition.TOP -> PaddingValues(start = cornerRadius, top = cornerRadius, end = cornerRadius, bottom = arrowHeight + cornerRadius)
                                    // ArrowPosition.BOTTOM: Пузырь под целью, стрелка указывает ВВЕРХ (стрелка на ВЕРХНЕЙ стороне пузыря)
                                    ArrowPosition.BOTTOM -> PaddingValues(start = cornerRadius, top = arrowHeight + cornerRadius, end = cornerRadius, bottom = cornerRadius)
                                })
                            ) {
                                content()
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

                    // Размещаем bubbleCardPlaceable в вычисленных окончательных координатах
                    layout(constraints.maxWidth, constraints.maxHeight) {
                        bubbleCardPlaceable.place(x = finalBubbleX.roundToInt(), y = finalBubbleY.roundToInt())
                    }
                }
            }
        }
    }
}

/**
 * Вспомогательная функция для получения Rect обычного View после его отрисовки.
 * Возвращает State<Rect?> который будет обновляться после каждого перекомпоновки View.
 *
 * @param view View, для которого нужно получить Rect.
 * @return State<Rect?> с границами View в оконных координатах.
 *
 * Примечание: В Compose Preview эта функция может работать непредсказуемо или не возвращать
 * корректные значения, так как она полагается на ViewTreeObserver нативной Android View.
 * Для получения координат AndroidView, встроенных в Compose с помощью AndroidView,
 * обычно более надежным является использование Modifier.onGloballyPositioned на самом AndroidView.
 */
@Composable
fun rememberViewRectInWindow(view: View?): State<Rect?> {
    val viewRect = remember { mutableStateOf<Rect?>(null) }

    DisposableEffect(view) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            if (view != null) {
                val location = IntArray(2)
                view.getLocationOnScreen(location)
                val width = view.width
                val height = view.height

                viewRect.value = Rect(
                    left = location[0].toFloat(),
                    top = location[1].toFloat(),
                    right = (location[0] + width).toFloat(),
                    bottom = (location[1] + height).toFloat()
                )
            }
        }

        view?.viewTreeObserver?.addOnGlobalLayoutListener(listener)

        onDispose {
            // Исправлено: используем removeOnGlobalLayoutListener
            view?.viewTreeObserver?.removeOnGlobalLayoutListener(listener)
        }
    }
    return viewRect
}

/**
 * Расширение для String, преобразующее шестнадцатеричную строку цвета в Int.
 * Поддерживает форматы "#RRGGBB" и "#AARRGGBB".
 */
fun String.toColorInt(): Int {
    val hex = this.removePrefix("#")
    return when (hex.length) {
        6 -> "FF$hex".toLong(16).toInt() // Добавляем полный альфа-канал, если он отсутствует
        8 -> hex.toLong(16).toInt()
        else -> throw IllegalArgumentException("Неверный формат шестнадцатеричной строки цвета: $this")
    }
}


@Preview(showBackground = true, device = Devices.PIXEL_4, name = "Bubble Screen Preview")
@Composable
fun BubblePreviewScreen() {
    // Состояние для хранения Rect для каждой цели
    var targetRectTopLeft by remember { mutableStateOf<Rect?>(null) }
    var targetRectTopRight by remember { mutableStateOf<Rect?>(null) }
    var targetRectBottomLeft by remember { mutableStateOf<Rect?>(null) }
    var targetRectBottomRight by remember { mutableStateOf<Rect?>(null) }
    var targetRectCenter by remember { mutableStateOf<Rect?>(null) }

    // Состояние для управления видимостью центрального пузыря
    var isCentralBubbleVisible by remember { mutableStateOf(true) }

    // Получаем Density в Composable-контексте
    val density = LocalDensity.current

    // Общие настройки для всех пузырей в этом предварительном просмотре
    val commonSettings = remember {
        BubbleCommonSettings(
            arrowWidth = DEFAULT_ARROW_WIDTH,
            arrowHeight = DEFAULT_ARROW_HEIGHT,
            cornerRadius = DEFAULT_CORNER_RADIUS,
            backgroundColor = DEFAULT_BACKGROUND_COLOR,
            horizontalScreenPadding = DEFAULT_HORIZONTAL_SCREEN_PADDING,
            verticalScreenPadding = DEFAULT_VERTICAL_SCREEN_PADDING,
            scrimColor = DEFAULT_SCRIM_COLOR,
            dismissOnScrimClick = true, // По умолчанию scrim кликабелен для закрытия в этом превью
            enterAnimationDurationMs = 400, // Увеличена продолжительность анимации появления
            exitAnimationDurationMs = 400    // Увеличена продолжительность анимации исчезновения
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.LightGray)) {

        // Цель 1: Верхний левый угол
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(80.dp)
                .background(Color.Blue)
                .onGloballyPositioned { coordinates ->
                    targetRectTopLeft = coordinates.boundsInWindow()
                },
            contentAlignment = Alignment.Center
        ) {
            Text("ВЛ", color = Color.White)
        }

        // Пузырь для верхнего левого угла (позиция СНИЗУ)
        Bubble(
            commonSettings = commonSettings,
            bubbleData = BubbleData(
                targetRect = targetRectTopLeft,
                arrowPosition = ArrowPosition.BOTTOM,
                arrowTargetOffset = 10.dp, // Пример: смещение стрелки на 10dp от цели
                content = {
                    Text("Пузырь снизу", color = Color.White, modifier = Modifier.padding(8.dp))
                }
            ),
            modifier = Modifier.heightIn(max = 100.dp) // Ограничение высоты
        )

        // Цель 2: Верхний правый угол
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(80.dp)
                .background(Color.Red)
                .onGloballyPositioned { coordinates ->
                    targetRectTopRight = coordinates.boundsInWindow()
                },
            contentAlignment = Alignment.Center
        ) {
            Text("ВП", color = Color.White)
        }

        // Пузырь для верхнего правого угла (позиция СЛЕВА)
        Bubble(
            commonSettings = commonSettings.copy(backgroundColor = Color.Red.copy(alpha = 0.8f)), // Переопределяем цвет фона
            bubbleData = BubbleData(
                targetRect = targetRectTopRight,
                arrowPosition = ArrowPosition.LEFT,
                arrowTargetOffset = -10.dp, // Пример: смещение стрелки на 10dp к цели
                content = {
                    Text("Пузырь слева", color = Color.White, modifier = Modifier.padding(8.dp))
                }
            ),
            modifier = Modifier.heightIn(max = 100.dp) // Ограничение высоты
        )

        // Цель 3: Нижний левый угол
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .size(80.dp)
                .background(Color.Green)
                .onGloballyPositioned { coordinates ->
                    targetRectBottomLeft = coordinates.boundsInWindow()
                },
            contentAlignment = Alignment.Center
        ) {
            Text("НЛ", color = Color.White)
        }

        // Пузырь для нижнего левого угла (позиция СПРАВА)
        Bubble(
            commonSettings = commonSettings.copy(backgroundColor = Color.Green.copy(alpha = 0.8f)),
            bubbleData = BubbleData(
                targetRect = targetRectBottomLeft,
                arrowPosition = ArrowPosition.RIGHT,
                arrowTargetOffset = 5.dp, // Пример: смещение стрелки на 5dp от цели
                content = {
                    Text("Пузырь справа", color = Color.White, modifier = Modifier.padding(8.dp))
                }
            ),
            modifier = Modifier.heightIn(max = 100.dp) // Ограничение высоты
        )

        // Цель 4: Нижний правый угол
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(80.dp)
                .background(Color.Magenta)
                .onGloballyPositioned { coordinates ->
                    targetRectBottomRight = coordinates.boundsInWindow()
                },
            contentAlignment = Alignment.Center
        ) {
            Text("НП", color = Color.White)
        }

        // Пузырь для нижнего правого угла (позиция СВЕРХУ)
        Bubble(
            commonSettings = commonSettings.copy(backgroundColor = Color.Magenta.copy(alpha = 0.8f)),
            bubbleData = BubbleData(
                targetRect = targetRectBottomRight,
                arrowPosition = ArrowPosition.TOP,
                arrowTargetOffset = -5.dp, // Пример: смещение стрелки на 5dp к цели
                content = {
                    Text("Пузырь сверху", color = Color.White, modifier = Modifier.padding(8.dp))
                }
            ),
            modifier = Modifier.heightIn(max = 100.dp) // Ограничение высоты
        )

        // Цель 5: Центр
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(80.dp)
                .background(Color.DarkGray)
                .onGloballyPositioned { coordinates ->
                    targetRectCenter = coordinates.boundsInWindow()
                },
            contentAlignment = Alignment.Center
        ) {
            Text("ЦЕНТР", color = Color.White)
        }

        // Пузырь для центральной цели (позиция СНИЗУ, как пример)
        Bubble(
            commonSettings = commonSettings,
            bubbleData = BubbleData(
                targetRect = targetRectCenter,
                arrowPosition = ArrowPosition.BOTTOM,
                isVisible = isCentralBubbleVisible, // Управляем видимостью
                onDismissRequest = { isCentralBubbleVisible = false }, // Колбэк для скрытия
                arrowTargetOffset = 15.dp, // Пример: смещение стрелки на 15dp от цели
                content = {
                    Text("Центральный пузырь", color = Color.White, modifier = Modifier.padding(8.dp))
                }
            ),
            modifier = Modifier.heightIn(max = 100.dp) // Ограничение высоты
        )

        // Кнопка для скрытия центрального пузыря
        androidx.compose.material3.Button( // Использовать полный путь для Button, чтобы избежать конфликта с AndroidButton
            onClick = { isCentralBubbleVisible = !isCentralBubbleVisible }, // Переключаем видимость
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Text(if (isCentralBubbleVisible) "Скрыть центральный пузырь" else "Показать центральный пузырь")
        }
    }
}

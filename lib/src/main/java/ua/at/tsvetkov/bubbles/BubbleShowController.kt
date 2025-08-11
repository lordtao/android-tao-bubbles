package ua.at.tsvetkov.bubbles

/**
 * Created by Alexandr Tsvetkov on 28.07.2025.
 */

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned

/**
 * Manages the state and showing of a sequence of bubbles to be shown on screen.
 *
 * This controller keeps track of the current bubble, its target component's position,
 * and the overall visibility of the bubble sequence. It provides methods to advance
 * through the bubbles, reset the sequence, or stop it entirely.
 *
 * It is recommended to create and manage instances of this controller using
 * the [rememberBubbleShowController] Composable function.
 *
 * @property bubbles The immutable list of [BubbleData] objects defining the sequence.
 * @property onFinished A lambda invoked when the last bubble in the sequence has been shown
 *                      and the show is considered finished.
 */
@Stable
class BubbleShowController(
    val settings: BubblesSettings = BubblesSettings(),
    private val bubbles: List<BubbleData>,
    val onFinished: () -> Unit = {},
) {
    private var currentBubbleIndex by mutableIntStateOf(0)

    /**
     * Indicates whether the bubble show sequence is currently active and should be visible.
     * Set to `false` when the sequence is completed or explicitly stopped.
     * This property is read-only from outside the controller.
     */
    var isVisible by mutableStateOf(true)
        private set

    /**
     * Internal map holding the screen bounds ([Rect]) of target components,
     * keyed by their unique identifiers (usually [BubbleData.id]).
     * This map is updated by the [Modifier.assignBubble] extension.
     */
    internal val targetRectsMap = mutableStateMapOf<String, Rect?>()

    /**
     * The [BubbleData] for the currently active bubble in the sequence.
     * Returns `null` if the sequence is out of bounds (e.g., completed).
     */
    val currentBubbleData: BubbleData?
        get() = bubbles.getOrNull(currentBubbleIndex)

    /**
     * The screen bounds ([Rect]) of the target component for the [currentBubbleData].
     * Returns `null` if there is no current bubble or if its target's bounds
     * have not yet been determined or registered via [Modifier.assignBubble].
     */
    val currentTargetRect: Rect?
        get() = currentBubbleData?.let { targetRectsMap[it.id] }

    init {
        val firstBubbleIdForShow = bubbles.indexOfFirst {
            it.isNotShowed()
        }.takeIf { it != -1 }

        if (firstBubbleIdForShow != null) {
            currentBubbleIndex = firstBubbleIdForShow
        } else {
            isVisible = false
        }
    }

    /**
     * Advances to the next bubble in the sequence.
     * If the current bubble is the last one, [isVisible] is set to `false`,
     * and the [onFinished] lambda is invoked.
     */
    fun showNext() {
        if (currentBubbleIndex < bubbles.size - 1) {
            currentBubbleIndex++
        } else {
            isVisible = false
            onFinished.invoke()
        }
    }

    /**
     * Start showing a sequence of bubbles that will be shown on the screen
     */
    @Composable
    fun ShowBubbles() {
        currentBubbleData?.let {bubbleData ->
            if (isVisible && bubbleData.isNotShowed()) {
                bubbleData.setShowed()
                Bubble(
                    bubbleData = bubbleData,
                    controller = this
                )
            }
        }
    }

    /**
     * Restarts the bubble show sequence.
     * All bubbles are marked as "not shown", the index is reset to the first bubble,
     * and [isVisible] is set to `true`.
     */
    fun restartShow() {
        bubbles.forEach { it.setNotShowed() }
        currentBubbleIndex = 0
        isVisible = true
    }

    /**
     * Stops the bubble show sequence immediately.
     * All bubbles are marked as "shown" (to prevent them from reappearing on a reset if not desired),
     * the index is reset, and [isVisible] is set to `false`.
     */
    fun stopShow() {
        bubbles.forEach { it.setShowed() }
        currentBubbleIndex = 0
        isVisible = false
    }

    /**
     * Updates the screen bounds ([Rect]) for a target component associated with a given key.
     * This function is typically called by the [Modifier.assignBubble] extension.
     *
     * @param key The unique identifier for the target component (usually [BubbleData.id]).
     * @param rect The new screen bounds for the target component.
     */
    internal fun updateTargetRect(key: String, rect: Rect?) {
        targetRectsMap[key] = rect
    }
}

/**
 * Creates and remembers a [BubbleShowController] instance.
 *
 * This Composable function is the recommended way to instantiate a [BubbleShowController].
 * It uses [remember] to ensure that the same [BubbleShowController] instance is
 * retained across recompositions, preserving the current state of the bubble sequence.
 *
 * The controller will be re-created if the [bubbles] list instance changes.
 *
 * @param bubbles The list of [BubbleData] objects defining the sequence of bubbles to be shown.
 *                If this list instance changes, the [BubbleShowController] will be reset.
 * @param onFinished A lambda that will be invoked when the last bubble in the sequence
 *                   has been shown and the show is finished. Defaults to an empty lambda.
 * @return An instance of [BubbleShowController] that manages the bubble show sequence.
 */
@Composable
fun rememberBubbleShowController(
    settings: BubblesSettings,
    bubbles: List<BubbleData>,
    onFinished: () -> Unit = {},
): BubbleShowController {
    return remember(bubbles) {
        BubbleShowController(settings, bubbles, onFinished)
    }
}

/**
 * A [Modifier] extension function that associates a Composable with a specific [BubbleData]
 * and registers its screen bounds with the provided [BubbleShowController].
 *
 * This allows the [BubbleShowController] to determine the position of the target
 * Composable on the screen, which is essential for correctly positioning the bubble
 * that points to or highlights this Composable.
 *
 * Usage:
 * ```
 * Text(
 *     text = "Target Text",
 *     modifier = Modifier.assignBubble(myBubbleController, myBubbleDataForThisText)
 * )
 * ```
 *
 * @param controller The [BubbleShowController] that manages the bubble sequence.
 * @param bubbleData The [BubbleData] instance associated with the Composable
 *                   to which this modifier is applied. The `id` of this `bubbleData`
 *                   is used as the key to store its bounds.
 * @return A new [Modifier] that includes the logic for tracking the Composable's position.
 */
fun Modifier.assignBubble(controller: BubbleShowController, bubbleData: BubbleData): Modifier =
    this.onGloballyPositioned { coordinates ->
        controller.updateTargetRect(bubbleData.id, coordinates.boundsInWindow())
    }

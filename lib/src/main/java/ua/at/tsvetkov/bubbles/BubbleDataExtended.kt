package ua.at.tsvetkov.bubbles

/**
 * Created by Alexandr Tsvetkov on 12.08.2025.
 */

/**
 * A data object containing data and behavior specific to each Bubble instance.
 */
data class BubbleDataExtended(
    val data: BubbleData,
    val settings: BubblesSettings = BubblesSettings(),
) {
    fun getId() = data.id
}
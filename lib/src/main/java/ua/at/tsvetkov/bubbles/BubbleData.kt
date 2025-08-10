package ua.at.tsvetkov.bubbles

import androidx.compose.runtime.Composable
import ua.at.tsvetkov.application.AppConfig
import ua.at.tsvetkov.application.AppConfig.SAVE

/**
 * Created by Alexandr Tsvetkov on 01.08.2025.
 */

/**
 * A data object containing data and behavior specific to each Bubble instance.
 */
data class BubbleData(
    val id: String,
    val arrowPosition: ArrowPosition = ArrowPosition.BOTTOM,
    val content: @Composable (onActionClick: () -> Unit, onStopShowRequest: () -> Unit) -> Unit,
) {

    private val key = "BUBBLE_NOT_SHOWED_${id.uppercase().replace(Regex("[ .-]"), "_")}"

    fun isNotShowed() = AppConfig.getBoolean(key, defValue = true)

    fun setShowed() {
        AppConfig.putBoolean(key, false, SAVE)
    }

    fun setNotShowed() {
        AppConfig.putBoolean(key, true, SAVE)
    }

}
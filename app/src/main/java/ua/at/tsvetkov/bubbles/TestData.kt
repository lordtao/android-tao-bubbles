package ua.at.tsvetkov.bubbles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Created by Alexandr Tsvetkov on 10.08.2025.
 */
object TestData {

    val testSettings = BubblesSettings(
        scrimColor = Color(0x22002EFF),
        backgroundColor = Color(0xFFFFDAB7),
        bubbleBorderColor = Color.Black,
        bubbleBorderWidth = 2.dp
    )

    val testBubbles = listOf(
        BubbleData(
            id = "bubble_bottom", 
            arrowPosition = ArrowPosition.BOTTOM,
            content = { onActionClick ->
                Text(
                    "bubble bottom", color = Color.Black, modifier = Modifier
                        .padding(8.dp)
                        .clickable { onActionClick() })
            }
        ),
        BubbleData(
            id = "bubble_left",
            arrowPosition = ArrowPosition.LEFT,
            content = { onActionClick ->
                Text(
                    "bubble left", color = Color.Black, modifier = Modifier
                        .padding(8.dp)
                        .clickable { onActionClick() })
            }
        ),
        BubbleData(
            id = "bubble_right",
            arrowPosition = ArrowPosition.RIGHT,
            content = { onActionClick ->
                Text(
                    "bubble right", color = Color.Black, modifier = Modifier
                        .padding(8.dp)
                        .clickable { onActionClick() })
            }
        ),
        BubbleData(
            id = "bubble_top",
            arrowPosition = ArrowPosition.TOP,
            content = { onActionClick ->
                Text(
                    "bubble top", color = Color.Black, modifier = Modifier
                        .padding(8.dp)
                        .clickable { onActionClick() })
            }
        ),
        BubbleData(
            id = "bubble_center",
            content = { onActionClick ->
                Text(
                    "bubble center", color = Color.Black, modifier = Modifier
                        .padding(8.dp)
                        .clickable { onActionClick() }
                )
            }
        ),
    )

}
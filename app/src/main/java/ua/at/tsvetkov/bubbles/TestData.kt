package ua.at.tsvetkov.bubbles

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
            content = { onActionClick, onStopShowRequest ->
                MyContent(onStopShowRequest, onActionClick)
            }
        ),
        BubbleData(
            id = "bubble_left",
            arrowPosition = ArrowPosition.LEFT,
            content = { onActionClick, onStopShowRequest ->
                MyContent(onStopShowRequest, onActionClick)
            }
        ),
        BubbleData(
            id = "bubble_right",
            arrowPosition = ArrowPosition.RIGHT,
            content = { onActionClick, onStopShowRequest ->
                MyContent(onStopShowRequest, onActionClick)
            }
        ),
        BubbleData(
            id = "bubble_top",
            arrowPosition = ArrowPosition.TOP,
            content = { onActionClick, onStopShowRequest ->
                MyContent(onStopShowRequest, onActionClick)
            }
        ),
        BubbleData(
            id = "bubble_center",
            content = { onActionClick, onStopShowRequest ->
                MyContent(onStopShowRequest, onActionClick)
            }
        ),
    )

    @Composable
    private fun MyContent(onStopShowRequest: () -> Unit, onActionClick: () -> Unit) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .width(200.dp),
        ) {
            Text("Example of bubble help. You can insert your Composable object instead.", color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp)) // <<< Добавлен Spacer
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        onStopShowRequest() // Call onStopShowRequest
                    }) {
                    Text("Stop Show", color = Color.White)
                }
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = {
                        onActionClick() // Call onActionClick for "Next"
                    }) {
                    Text("Next", color = Color.White)
                }
            }
        }
    }

}

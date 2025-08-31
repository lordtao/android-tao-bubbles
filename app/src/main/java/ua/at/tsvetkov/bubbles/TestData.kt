package ua.at.tsvetkov.bubbles

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ua.at.tsvetkov.bubbles.compose.ArrowPosition
import ua.at.tsvetkov.bubbles.compose.BubbleData
import ua.at.tsvetkov.bubbles.compose.BubblesSettings
import ua.at.tsvetkov.bubbles.ui.theme.OrangeVeryLight
import ua.at.tsvetkov.bubbles.ui.theme.Purple40

/**
 * Created by Alexandr Tsvetkov on 10.08.2025.
 */

val testSettings = BubblesSettings(
    scrimColor = Color(0x22002EFF),
    backgroundColor = OrangeVeryLight,
    bubbleBorderColor = Color.Black,
    bubbleBorderWidth = 2.dp,
)

val testBubbles = listOf(
    BubbleData(
        id = "Bubble 1",
        arrowPosition = ArrowPosition.BOTTOM,
        content = { onDismissClick, onStopShowRequest ->
            MyContent("Bubble 1", onDismissClick, onStopShowRequest)
        }
    ),
    BubbleData(
        id = "Bubble 2",
        arrowPosition = ArrowPosition.LEFT,
        content = { onDismissClick, onStopShowRequest ->
            MyContent("Bubble 2", onDismissClick, onStopShowRequest)
        }
    ),
    BubbleData(
        id = "Bubble 3",
        arrowPosition = ArrowPosition.RIGHT,
        content = { onDismissClick, onStopShowRequest ->
            MyContent("Bubble 3", onDismissClick, onStopShowRequest)
        }
    ),
    BubbleData(
        id = "Bubble 4",
        arrowPosition = ArrowPosition.TOP,
        content = { onDismissClick, onStopShowRequest ->
            MyContent("Bubble 4", onDismissClick, onStopShowRequest)
        }
    ),
    BubbleData(
        id = "Bubble 5",
        content = { onDismissClick, onStopShowRequest ->
            MyContent("Bubble 5", onDismissClick, onStopShowRequest)
        }
    ),
)

@Composable
private fun MyContent(title: String, onDismissClick: () -> Unit, onStopShowRequest: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Text(title, color = Purple40, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Example of bubble help. You can insert your Composable object instead. This is just an example. Text can be multiline.", color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
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
                    onDismissClick() // Call onDismissClick for "Next"
                }) {
                Text("Next", color = Color.White)
            }
        }
    }
}



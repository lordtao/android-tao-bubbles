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
import ua.at.tsvetkov.bubbles.compose.BubbleDataExtended
import ua.at.tsvetkov.bubbles.compose.BubblesSettings
import ua.at.tsvetkov.bubbles.ui.theme.Purple40

/**
 * Created by Alexandr Tsvetkov on 10.08.2025.
 */

val testBubblesExtended = listOf(
    BubbleDataExtended(
        BubbleData(
            id = "Bubble Extended Settings 1",
            arrowPosition = ArrowPosition.RIGHT,
            content = { onDismissClick, onStopShowRequest ->
                MyContentExtended("Bubble Extended Settings 1", onDismissClick, onStopShowRequest)
            }
        ),
        BubblesSettings(
            scrimColor = Color(0x3366FA67),
            backgroundColor = Color(0xFF66FA67),
            bubbleBorderColor = Color(0xFF008500),
            bubbleBorderWidth = 2.dp,
            cornerRadius = 2.dp,
            arrowWidth = 10.dp,
            arrowHeight = 30.dp,
        )
    ),
    BubbleDataExtended(
        BubbleData(
            id = "Bubble Extended Settings 2",
            arrowPosition = ArrowPosition.LEFT,
            content = { onDismissClick, onStopShowRequest ->
                MyContentExtended("Bubble Extended Settings 2", onDismissClick, onStopShowRequest)
            }
        ),
        BubblesSettings(
            scrimColor = Color(0x33EEA4FF),
            backgroundColor = Color(0xFFEEA4FF),
            bubbleBorderColor = Color(0xFF9C00BE),
            cornerRadius = 32.dp,
            arrowWidth = 30.dp,
            arrowHeight = 10.dp,
        )
    ),
    BubbleDataExtended(
        BubbleData(
            id = "Bubble Extended Settings 3",
            arrowPosition = ArrowPosition.BOTTOM,
            content = { onDismissClick, onStopShowRequest ->
                MyContentExtended("Bubble Extended Settings 3", onDismissClick, onStopShowRequest)
            }
        ),
        BubblesSettings(
            scrimColor = Color(0x33FFEB3B),
            backgroundColor = Color(0xFFFFEB3B),
            bubbleBorderWidth = 4.dp,
            bubbleBorderColor = Color(0xFF9C00BE),
        )
    ),
)

@Composable
private fun MyContentExtended(title: String, onDismissClick: () -> Unit, onStopShowRequest: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Text(title, color = Purple40, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Example of bubble extended settings", color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    onStopShowRequest() // Call onStopShowRequest
                }) {
                Text("Stop", color = Color.White)
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



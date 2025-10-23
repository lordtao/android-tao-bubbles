package ua.at.tsvetkov.bubbles.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Created by Alexandr Tsvetkov on 23.10.2025.
 */
@Preview(showBackground = true, device = Devices.NEXUS_6, name = "Bubble Screen Preview")
@Composable
fun BubblePreview() {
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
                bubbleBorderColor = Color.Black,
                bubbleBorderWidth = 1.dp
            ),
            bubbleData = BubbleData(
                id = "TopStart",
                arrowPosition = ArrowPosition.BOTTOM,
                content = { onDismissClick, onStopShowRequest ->
                    Text(
                        "Bubble TopStart",
                        color = Color.White,
                        modifier =
                        Modifier
                            .padding(8.dp)
                            .clickable { onDismissClick() }
                    )
                }
            ),
            targetComponentRect = targetRectTopLeft,
            isVisible = true
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
                bubbleBorderColor = Color.Black,
                bubbleBorderWidth = 1.dp
            ),
            bubbleData = BubbleData(
                id = "TopEnd",
                arrowPosition = ArrowPosition.LEFT,
                content = { onDismissClick, onStopShowRequest ->
                    Text(
                        "Bubble TopEnd",
                        color = Color.Black,
                        modifier =
                        Modifier
                            .padding(8.dp)
                            .clickable { onDismissClick() }
                    )
                }
            ),
            targetComponentRect = targetRectTopRight,
            isVisible = true
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
                bubbleBorderColor = Color.Black,
                bubbleBorderWidth = 1.dp
            ),
            bubbleData = BubbleData(
                id = "bubble3",
                arrowPosition = ArrowPosition.RIGHT,
                content = { onDismissClick, onStopShowRequest ->
                    Text(
                        "Bubble BottomStart",
                        color = Color.Black,
                        modifier =
                        Modifier
                            .padding(8.dp)
                            .clickable { onDismissClick() }
                    )
                }
            ),
            targetComponentRect = targetRectBottomLeft,
            isVisible = true
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
                bubbleBorderColor = Color.Black,
                bubbleBorderWidth = 1.dp,
                arrowHeight = 40.dp,
                arrowWidth = 40.dp
            ),
            bubbleData = BubbleData(
                id = "bubble4",
                arrowPosition = ArrowPosition.TOP,
                content = { onDismissClick, onStopShowRequest ->
                    Text(
                        "Bubble BottomEnd",
                        color = Color.White,
                        modifier =
                        Modifier
                            .padding(8.dp)
                            .clickable { onDismissClick() }
                    )
                }
            ),
            targetComponentRect = targetRectBottomRight,
            isVisible = true
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
                bubbleBorderWidth = 8.dp,
                arrowTargetOffset = 5.dp
            ),
            bubbleData = BubbleData(
                id = "bubbleCenter",
                arrowPosition = ArrowPosition.BOTTOM,
                content = { onDismissClick, onStopShowRequest ->
                    Text(
                        "Bubble Center with Border",
                        color = Color.White,
                        modifier =
                        Modifier
                            .padding(8.dp)
                            .clickable { onDismissClick() }
                    )
                }
            ),
            targetComponentRect = targetRectCenter,
            onDismissRequest = { isCentralBubbleVisible = false },
            isVisible = isCentralBubbleVisible
        )
    }
}

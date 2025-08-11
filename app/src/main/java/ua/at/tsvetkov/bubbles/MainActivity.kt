package ua.at.tsvetkov.bubbles

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ua.at.tsvetkov.bubbles.TestData.testBubbles
import ua.at.tsvetkov.bubbles.TestData.testSettings
import ua.at.tsvetkov.bubbles.ui.theme.MyApplicationTheme
import ua.at.tsvetkov.bubbles.ui.theme.Purple40

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .background(Color(0xFFF5F5F5)), // Lightest Gray for outer background
                        contentAlignment = Alignment.Center
                    ) {
                        BubblesShowExample()
                    }
                }
            }
        }
    }
}

@Composable
fun BubblesShowExample() {

    val bubblesSettings = remember { testSettings }
    val bubblesData = remember { testBubbles }
    val bubbleShowController = rememberBubbleShowController(
        settings = bubblesSettings,
        bubbles = bubblesData,
        onFinished = {
            // Actions to perform after all bubbles are completed
            Log.d("BubblesShowExample", "All bubbles completed")
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA))
    ) {

        // Differently positioned UI components

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(100.dp)
                .background(Color(0xFF29B6F6))
                // Transferring data about this component to the controller and
                // associating it with a key from the pre-initialized BubbleData data
                .assignBubble(controller = bubbleShowController, bubbleData = bubblesData[0]),
            contentAlignment = Alignment.Center
        ) {
            Text("TopStart", color = Color.White)
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(100.dp)
                .background(Color(0xFFFFB74D))
                .assignBubble(controller = bubbleShowController, bubbleData = bubblesData[1]),
            contentAlignment = Alignment.Center
        ) {
            Text("TopEnd", color = Purple40)
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .size(100.dp)
                .background(Color(0xFFFFA726))
                .assignBubble(controller = bubbleShowController, bubbleData = bubblesData[2]),
            contentAlignment = Alignment.Center
        ) {
            Text("BottomStart", color = Purple40)
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(100.dp)
                .background(Color(0xFF00ACC1))
                .assignBubble(controller = bubbleShowController, bubbleData = bubblesData[3]),
            contentAlignment = Alignment.Center
        ) {
            Text("BottomEnd", color = Color.White)
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(100.dp)
                .background(Color(0xFF0277BD))
                .assignBubble(controller = bubbleShowController, bubbleData = bubblesData[4]),
            contentAlignment = Alignment.Center
        ) {
            Text("Center", color = Color.White)
        }

        Button(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 224.dp),
            onClick = {
                bubbleShowController.restartShow()
            }) {
            Text("Restart Show", color = Color.White)
        }

        // Start showing the bubbles show

        bubbleShowController.ShowBubbles()

        // Another production of the show

//        bubbleShowController.ShowBubble {
//            Bubble(
//                settings = bubblesSettings,
//                bubbleData = it,
//                onDismissRequest = {
//                    // Show next bubble or/and add something else
//                    bubbleShowController.showNext()
//                },
//                onStopShowRequest = {
//                    // Stop showing bubbles
//                    bubbleShowController.stopShow()
//                },
//                targetComponentRect = bubbleShowController.currentTargetRect,
//            )
//        }

    }
}


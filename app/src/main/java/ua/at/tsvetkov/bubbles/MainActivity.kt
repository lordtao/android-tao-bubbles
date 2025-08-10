package ua.at.tsvetkov.bubbles

import android.os.Bundle
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.at.tsvetkov.bubbles.TestData.testBubbles
import ua.at.tsvetkov.bubbles.TestData.testSettings
import ua.at.tsvetkov.bubbles.ui.theme.MyApplicationTheme
import ua.at.tsvetkov.util.logger.Log

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
                            .background(Color.Cyan),
                        contentAlignment = Alignment.Center
                    ) {
                        Greeting(
                            name = "Android"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    val bubblesSettings = remember { testSettings }

    val bubblesData = remember { testBubbles }

    val bubbleShowController = rememberBubbleShowController(
        settings = bubblesSettings,
        bubbles = bubblesData,
        onFinished = {
            // Actions to perform after all bubbles are completed
            Log.d("All bubbles completed")
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {

        // Differently positioned UI components

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(100.dp)
                .background(Color.Blue)
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
                .background(Color.Red)
                .assignBubble(controller = bubbleShowController, bubbleData = bubblesData[1]),
            contentAlignment = Alignment.Center
        ) {
            Text("TopEnd", color = Color.White)
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .size(100.dp)
                .background(Color.Green)
                .assignBubble(controller = bubbleShowController, bubbleData = bubblesData[2]),
            contentAlignment = Alignment.Center
        ) {
            Text("BottomStart", color = Color.Black)
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(100.dp)
                .background(Color.Magenta)
                .assignBubble(controller = bubbleShowController, bubbleData = bubblesData[3]),
            contentAlignment = Alignment.Center
        ) {
            Text("BottomEnd", color = Color.Black)
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(100.dp)
                .background(Color.DarkGray)
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
                bubbleShowController.resetShow()
            }) {
            Text("Reset Show", color = Color.White)
        }

        // Initializing and showing bubbles

        bubbleShowController.ShowBubble { bubblesData ->
            Bubble(
                bubbleData = bubblesData,
                controller = bubbleShowController
            )
        }

        // Another production of the show

//        bubbleShowController.ShowBubble {
//            Bubble(
//                settings = bubblesSettings,
//                bubbleData = it,
//                onDismissRequest = {
//                    // Show next bubble or/and add something else
//                    bubbleShowController.showNext()
//                },
//                targetComponentRect = bubbleShowController.currentTargetRect,
//            )
//        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.Cyan),
            contentAlignment = Alignment.Center
        ) {
            Greeting(
                name = "Android"
            )
        }
    }
}


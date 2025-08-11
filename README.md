# android-tao-bubbles
=====================

A lightweight Jetpack Compose library for displaying customizable "bubbles", "bubbles show" or "tooltips" that can point to specific UI elements. 
This is useful for tutorials, feature highlights, or contextual help.

## Features

*   **Customizable Appearance**: Control arrow position, size, corner radius, colors, borders, and more.
*   **Flexible Positioning**: Bubbles can point to any side of a target composable (`LEFT`, `RIGHT`, `TOP`, `BOTTOM`).
*   **Screen Boundary Awareness**: Bubbles adjust their position to stay within screen bounds.
*   **Sequential Display**: Use `BubbleShowController` to easily display a series of bubbles.
*   **Enter/Exit Animations**: Smooth animations for bubble appearance and disappearance.
*   **Scrim Background**: Optional scrim to dim the background when a bubble is shown.
*   **Declarative API**: Built with Jetpack Compose for a modern Android UI.

[Download the latest release and demo](https://github.com/lordtao/android-tao-bubble/releases)

[Licence](https://opensource.org/license/mit)

![Preview](media/Preview.png)

![Video of BubbleDemo](media/screen_recording.webm)

###  `Bubble`

**Example Usage (Single Bubble):**

Create common bubble/s settings:

```kotlin
val testSettings = BubblesSettings(
        scrimColor = Color(0x22002EFF),
        backgroundColor = OrangeVeryLight,
        bubbleBorderColor = Color.Black,
        bubbleBorderWidth = 2.dp
    )
```

Prepare your bubble content:

```kotlin
val bubbleData = BubbleData(
    id = "Bubble id", // Must be unique for save the bubble state - shown or no
    arrowPosition = ArrowPosition.BOTTOM,
    content = { onDismissClick, onStopShowRequest ->
        // any composable content inside the Bubble
    }
)
```
Use in your code

```kotlin

@Composable
fun SingleBubbleExample() {
    var showBubble by remember { mutableStateOf(false) }
    var targetRect by remember { mutableStateOf<Rect?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { showBubble = !showBubble },
            modifier = Modifier
                .align(Alignment.Center)
                .onGloballyPositioned { coordinates ->
                    targetRect = coordinates.boundsInWindow() // Get target component Rect
                }
        ) {
            Text("Show Bubble")
        }

        if (showBubble && targetRect != null) {
            Bubble(
                targetComponentRect = targetRect,
                bubbleData = bubbleData,
                settings = testSettings,
                isVisible = showBubble,
                onDismissRequest = { showBubble = false }
            )
        }
    }
}

```

**Example Usage (Sequence of Bubbles):**

Use `BubbleShowController` to manages the display of a sequence of bubbles (Bubbles show).

**Key Features:**

*   Maintains a queue of `BubbleData`.
*   Shows bubbles one by one.
*   Handles dismissal and advancing to the next bubble.
*   Cancel all shows.

Prepare your bubble content:

```kotlin
val testBubbles = listOf(
    BubbleData(
        id = "Bubble 1",
        arrowPosition = ArrowPosition.BOTTOM,
        content = { onDismissClick, onStopShowRequest ->
            // any composable content inside the Bubble
            MyContent("Bubble 1", onDismissClick, onStopShowRequest)
        }
    ),
    BubbleData(
        id = "Bubble 2",
        arrowPosition = ArrowPosition.LEFT,
        content = { onDismissClick, onStopShowRequest ->
            // any composable content inside the Bubble
            MyContent("Bubble 2", onDismissClick, onStopShowRequest)
        }
    ),
    BubbleData(
        id = "Bubble 3",
        arrowPosition = ArrowPosition.RIGHT,
        content = { onDismissClick, onStopShowRequest ->
            // any composable content inside the Bubble
            MyContent("Bubble 3", onDismissClick, onStopShowRequest)
        }
    )
)
```
Use in your code

```kotlin
@Composable
fun BubbleSequenceExample() {

    // Bubbles show preparing
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

    // Your UI
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
                .assignBubble(controller = bubbleShowController, bubbleData = bubblesData[2]),
            contentAlignment = Alignment.Center
        ) {
            Text("BottomStart", color = Purple40)
        }
        
        // Restart show button
        
        Button(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 224.dp),
            onClick = {
                bubbleShowController.restartShow()
            }) {
            Text("Restart Show", color = Color.White)
        }

        // Display the current bubble from the controller
        // The Bubble composable with a controller automatically handles visibility and settings.
        
        bubbleShowController.ShowBubbles()

    }
}

```

## Contribution

Feel free to open issues or submit pull requests!



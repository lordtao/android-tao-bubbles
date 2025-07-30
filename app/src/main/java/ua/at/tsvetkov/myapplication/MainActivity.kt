package ua.at.tsvetkov.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.at.tsvetkov.babbles.ArrowPosition
import ua.at.tsvetkov.babbles.Bubble
import ua.at.tsvetkov.babbles.BubbleCommonSettings
import ua.at.tsvetkov.babbles.BubbleData
import ua.at.tsvetkov.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding) // Применяем padding здесь
                            .fillMaxSize()
                            .background(Color.Cyan),       // Занимаем все доступное пространство
                        contentAlignment = Alignment.Center // Центрируем контент этого Box
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
// Состояние для хранения Rect для каждой цели
    var targetRectTopLeft by remember { mutableStateOf<Rect?>(null) }
    var targetRectTopRight by remember { mutableStateOf<Rect?>(null) }
    var targetRectBottomLeft by remember { mutableStateOf<Rect?>(null) }
    var targetRectBottomRight by remember { mutableStateOf<Rect?>(null) }
    var targetRectCenter by remember { mutableStateOf<Rect?>(null) }

    // Состояние для управления видимостью центрального пузыря
    var isCentralBubbleVisible by remember { mutableStateOf(true) }

    // Общие настройки для всех пузырей в этом предварительном просмотре
    val commonSettings = remember {
        BubbleCommonSettings(
            enterAnimationDurationMs = 0, // Отключена анимация появления
            exitAnimationDurationMs = 0    // Отключена анимация исчезновения
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.LightGray)) {

        // Цель 1: Верхний левый угол
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(80.dp)
                .background(Color.Blue)
                .onGloballyPositioned { coordinates ->
                    targetRectTopLeft = coordinates.boundsInWindow()
                },
            contentAlignment = Alignment.Center
        ) {
            Text("ВЛ", color = Color.White)
        }

        // Пузырь для верхнего левого угла (позиция СНИЗУ)
        Bubble(
            commonSettings = commonSettings,
            bubbleData = BubbleData(
                key = "bubble1", // Пример ключа
                targetRect = targetRectTopLeft,
                arrowPosition = ArrowPosition.BOTTOM,
                isVisible = true,
                onDismissRequest = { /* do nothing for preview */ },
                content = {
                    Text("Пузырь снизу", color = Color.White, modifier = Modifier.padding(8.dp))
                }
            ),
            modifier = Modifier.heightIn(max = 100.dp) // Ограничение высоты
        )

        // Цель 2: Верхний правый угол
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(80.dp)
                .background(Color.Red)
                .onGloballyPositioned { coordinates ->
                    targetRectTopRight = coordinates.boundsInWindow()
                },
            contentAlignment = Alignment.Center
        ) {
            Text("ВП", color = Color.White)
        }

        // Пузырь для верхнего правого угла (позиция СЛЕВА)
        Bubble(
            commonSettings = commonSettings.copy(backgroundColor = Color.Red.copy(alpha = 0.8f)), // Переопределяем цвет фона
            bubbleData = BubbleData(
                key = "bubble2", // Пример ключа
                targetRect = targetRectTopRight,
                arrowPosition = ArrowPosition.LEFT,
                isVisible = true,
                onDismissRequest = { /* do nothing for preview */ },
                content = {
                    Text("Пузырь слева", color = Color.White, modifier = Modifier.padding(8.dp))
                }
            ),
            modifier = Modifier.heightIn(max = 100.dp) // Ограничение высоты
        )

        // Цель 3: Нижний левый угол
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .size(80.dp)
                .background(Color.Green)
                .onGloballyPositioned { coordinates ->
                    targetRectBottomLeft = coordinates.boundsInWindow()
                },
            contentAlignment = Alignment.Center
        ) {
            Text("НЛ", color = Color.White)
        }

        // Пузырь для нижнего левого угла (позиция СПРАВА)
        Bubble(
            commonSettings = commonSettings.copy(backgroundColor = Color.Green.copy(alpha = 0.8f)),
            bubbleData = BubbleData(
                key = "bubble3", // Пример ключа
                targetRect = targetRectBottomLeft,
                arrowPosition = ArrowPosition.RIGHT,
                isVisible = true,
                onDismissRequest = { /* do nothing for preview */ },
                content = {
                    Text("Пузырь справа", color = Color.White, modifier = Modifier.padding(8.dp))
                }
            ),
            modifier = Modifier.heightIn(max = 100.dp) // Ограничение высоты
        )

        // Цель 4: Нижний правый угол
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(80.dp)
                .background(Color.Magenta)
                .onGloballyPositioned { coordinates ->
                    targetRectBottomRight = coordinates.boundsInWindow()
                },
            contentAlignment = Alignment.Center
        ) {
            Text("НП", color = Color.White)
        }

        // Пузырь для нижнего правого угла (позиция СВЕРХУ)
        Bubble(
            commonSettings = commonSettings.copy(backgroundColor = Color.Magenta.copy(alpha = 0.8f)),
            bubbleData = BubbleData(
                key = "bubble4", // Пример ключа
                targetRect = targetRectBottomRight,
                arrowPosition = ArrowPosition.TOP,
                isVisible = true,
                onDismissRequest = { /* do nothing for preview */ },
                content = {
                    Text("Пузырь сверху", color = Color.White, modifier = Modifier.padding(8.dp))
                }
            ),
            modifier = Modifier.heightIn(max = 100.dp) // Ограничение высоты
        )

        // Цель 5: Центр
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(80.dp)
                .background(Color.DarkGray)
                .onGloballyPositioned { coordinates ->
                    targetRectCenter = coordinates.boundsInWindow()
                },
            contentAlignment = Alignment.Center
        ) {
            Text("ЦЕНТР", color = Color.White)
        }

        // Пузырь для центральной цели (позиция СНИЗУ, как пример)
        Bubble(
            commonSettings = commonSettings,
            bubbleData = BubbleData(
                key = "bubbleCenter", // Пример ключа
                targetRect = targetRectCenter,
                arrowPosition = ArrowPosition.BOTTOM,
                isVisible = isCentralBubbleVisible, // Управляем видимостью
                onDismissRequest = { isCentralBubbleVisible = false }, // Колбэк для скрытия
                content = {
                    Text("Центральный пузырь", color = Color.White, modifier = Modifier.padding(8.dp)
                        .clickable{
                            isCentralBubbleVisible = false
                        })
                }
            ),
            modifier = Modifier.heightIn(max = 100.dp) // Ограничение высоты
        )
}
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding) // Применяем padding здесь
                .fillMaxSize()
                .background(Color.Cyan),       // Занимаем все доступное пространство
            contentAlignment = Alignment.Center // Центрируем контент этого Box
        ) {
            Greeting(
                name = "Android"
            )
        }
    }
}

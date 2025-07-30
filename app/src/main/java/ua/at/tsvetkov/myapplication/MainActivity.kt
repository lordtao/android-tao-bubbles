package ua.at.tsvetkov.myapplication

import android.graphics.fonts.FontStyle
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.at.tsvetkov.babbles.ArrowPosition
import ua.at.tsvetkov.babbles.Bubble
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
    var targetRect by remember { mutableStateOf<Rect?>(null) }
    var isCentralBubbleVisible by remember { mutableStateOf(true) }
    Box(
        modifier = modifier
            .size(200.dp)
            .background(Color.Yellow)
            .padding(32.dp)
            .onGloballyPositioned { coordinates ->
                targetRect = coordinates.boundsInWindow()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Hello $name!",
            modifier = Modifier
                .background(Color.Magenta)
                .padding(32.dp),
            color = Color.White,
            fontWeight = FontWeight.Bold //
        )
    }
    Bubble(
        targetRect = targetRect,
        arrowPosition = ArrowPosition.BOTTOM,
        modifier = Modifier.heightIn(max = 100.dp), // Ограничение высоты
        horizontalScreenPadding = 16.dp, // Отступ от краев экрана
        verticalScreenPadding = 16.dp,   // Отступ от краев экрана
        isVisible = isCentralBubbleVisible, // Управляем видимостью
        scrimColor = Color(0x52000000), // Черный с 32% прозрачностью
        arrowTargetOffset = 8.dp, // Пример: смещение стрелки на 15dp от цели
        content = {
            Text("Центральный пузырь", color = Color.White, modifier = Modifier.padding(8.dp).clickable {
                isCentralBubbleVisible = false
            })
        }
    )
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

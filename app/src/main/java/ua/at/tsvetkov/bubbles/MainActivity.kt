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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ua.at.tsvetkov.bubbles.compose.assignBubble
import ua.at.tsvetkov.bubbles.compose.rememberBubbleShowController
import ua.at.tsvetkov.bubbles.compose.rememberBubbleShowExtendedController
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
package com.pavit.wave

import android.R
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pavit.wave.ui.theme.WaveTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WaveTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NiagaraPrototype()
                }
            }
        }
    }
}
@Preview
@Composable
fun NiagaraPrototype() {
    // 1. The Data: A to Z, plus special chars
    val alphabet = remember {
        listOf('⭐', '#') + ('A'..'Z').toList()
    }

    // 2. State: Where is the finger? (Y-axis only for now)
    // null means the user isn't touching the screen
    var touchY by remember { mutableStateOf<Float?>(null) }

    // Debug helper: Visualizing the touch line
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Dark background to see white text
    ) {

        // This is the "Touch Surface" - it covers the whole right side
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(100.dp) // The touch area width
                .align(Alignment.CenterEnd)
                .background(Color.White.copy(alpha = 0.05f)) // Faint visible touch area
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { change ->
                            touchY = change.y
                        },
                        onDrag = { change, _ ->
                            touchY = change.position.y
                        },
                        onDragEnd = { touchY = null },
                        onDragCancel = { touchY = null }
                    )
                }
        )

        // The Alphabet Column
        Column(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalArrangement = Arrangement.spacedBy(2.dp), // Tight spacing like the screenshots
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            alphabet.forEach { letter ->
                // We will add the animation logic here in the next step
                NiagaraLetter(letter = letter, isSelected = false)
            }
        }

        // Debug: Show a red line where the finger is
        if (touchY != null) {
            Log.d("Touch Y", touchY.toString())
            Box(
                modifier = Modifier
                    .offset(y = with(LocalDensity.current) { touchY!!.toDp() })
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.Red)
            )
        }
    }
}

@Composable
fun NiagaraLetter(
    letter: Char,
    isSelected: Boolean
) {
    Text(
        text = letter.toString(),
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Preview
@Composable
fun DpRuler(){
    Column (
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        val sizes = listOf(8, 16, 32, 64, 100, 150, 200)
        sizes.forEach { size ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Box(modifier = Modifier.size(size.dp).background(Color.Red))
            }
        }
    }
}

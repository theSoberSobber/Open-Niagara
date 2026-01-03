package com.pavit.wave

import android.R
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.exp

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
    val alphabet = remember {
        listOf('⭐', '#') + ('A'..'Z').toList()
    }

    var touchY by remember { mutableStateOf<Float?>(null) }
    var columnHeight by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        // Touch Surface
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(100.dp)
                .align(Alignment.CenterEnd)
                .background(Color.White.copy(alpha = 0.05f))
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
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .onGloballyPositioned { coordinates ->
                    columnHeight = coordinates.size.height.toFloat()
                },
            verticalArrangement = Arrangement.spacedBy(0.001.dp),
            horizontalAlignment = Alignment.End
        ) {
            alphabet.forEach { letter ->
                NiagaraLetter(
                    letter = letter,
                    touchY = touchY,
                    maxInfluenceDistance = columnHeight
                )
            }
        }

        // Debug: Show a red line where the finger is
        if (touchY != null) {
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

/**
 * Calculates how far left a letter should move based on distance from touch
 * The closer the touch, the more it moves left (elastic rope effect)
 */
fun calculateElasticOffset(touchY: Float, letterY: Float, maxInfluenceDistance: Float): Float {
    val distance = (touchY - letterY)

    // Maximum offset in dp
    val maxOffset = 120f;
    val decayFactor = 10f;

    // Normalize distance by the column height
    val normalizedDistance = distance / maxInfluenceDistance

    // Use exponential decay for smooth elastic effect
    // Letters closer to touch move MORE
    val influence = exp(-normalizedDistance * normalizedDistance * decayFactor)

    return maxOffset * influence
}

@Composable
fun NiagaraLetter(
    letter: Char,
    touchY: Float?,
    maxInfluenceDistance: Float
) {
    var letterCenterY by remember { mutableStateOf(0f) }

    // Calculate the horizontal offset based on distance from touch
    val targetOffset = remember(touchY, letterCenterY, maxInfluenceDistance) {
        if (touchY == null || maxInfluenceDistance == 0f) {
            0f
        } else {
            calculateElasticOffset(touchY, letterCenterY, maxInfluenceDistance)
        }
    }

    // Animate the offset with spring physics for elastic feel
    val animatedOffset by animateFloatAsState(
        targetValue = targetOffset,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "letterOffset"
    )

    Text(
        text = letter.toString(),
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .offset(x = -animatedOffset.dp) // Move LEFT (negative X)
            .onGloballyPositioned { coordinates ->
                val rect = coordinates.positionInRoot()
                letterCenterY = rect.y + (coordinates.size.height / 2f)
            }
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

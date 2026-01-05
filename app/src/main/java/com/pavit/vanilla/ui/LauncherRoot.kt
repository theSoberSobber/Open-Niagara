package com.pavit.vanilla.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun LauncherRoot(){
    // removing system bars padding from the modifier because i like the overlap
    Surface (modifier = Modifier.fillMaxSize(), color = Color.Transparent){
        LauncherScreen()
    }
}
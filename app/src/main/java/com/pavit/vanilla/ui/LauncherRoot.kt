package com.pavit.vanilla.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun LauncherRoot(){
    Surface (modifier = Modifier.fillMaxSize(), color = Color.Transparent){
        LauncherScreen()
    }
}
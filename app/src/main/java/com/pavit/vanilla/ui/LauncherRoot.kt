package com.pavit.vanilla.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pavit.vanilla.ui.screens.LauncherScreen

@Composable
fun LauncherRoot(){
    // this is the root, we'd wanna build the
    // navgraphs here and have a sealed state of screens here to navigate between

    // removing system bars padding from the modifier because i like the overlap
    Surface (modifier = Modifier.fillMaxSize(), color = Color.Transparent){
        LauncherScreen()
    }
}
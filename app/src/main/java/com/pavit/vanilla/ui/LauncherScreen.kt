package com.pavit.vanilla.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

// need hilt navigation compose dependency for injecting the view model here
@Composable
fun LauncherScreen(
    vm: LauncherViewModel = hiltViewModel()
){
    Box(modifier = Modifier.fillMaxSize()){
        Text(
            text = "meow",
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
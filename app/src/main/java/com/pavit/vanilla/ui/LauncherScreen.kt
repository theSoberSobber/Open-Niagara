package com.pavit.vanilla.ui

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.google.accompanist.drawablepainter.rememberDrawablePainter

// need hilt navigation compose dependency for injecting the view model here
@Composable
fun LauncherScreen(
    vm: LauncherViewModel = hiltViewModel()
){
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize().background(color = Color.Black.copy(0.13f))){
//        Text(
//            text = "meow",
//            color = Color.White,
//            modifier = Modifier.align(Alignment.Center)
//        )
        val apps = vm.apps.collectAsState().value
        Log.d("Apps Queried: ", apps.toString());
        LazyColumn(modifier = Modifier.align(Alignment.Center).fillMaxSize()){
            items(apps.size){
                AppRow(text = apps[it].name, packageName = apps[it].packageName, icon = apps[it].icon)
            }
        }
    }
}

@Composable
fun AppRow(text: String, packageName: String, icon: Drawable){
    val context = LocalContext.current
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // rememberDrawablePainter is from accompanist
        // to convert the drawable into bitmap
        Image(
            painter = rememberDrawablePainter(icon),
            contentDescription = packageName,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = Color.White,
            modifier = Modifier
                .clickable {
                    context.startActivity(
                        context.packageManager.getLaunchIntentForPackage(packageName)
                    )
                },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
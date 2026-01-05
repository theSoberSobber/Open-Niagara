package com.pavit.vanilla.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.pavit.vanilla.ui.components.AppList.AppList

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

        val groupedFilteredApps by vm.groupedFilteredApps.collectAsState()
        Log.d("GroupedFilteredApps2", groupedFilteredApps.toString())

        // app list
        AppList(groupedFilteredApps)

        // app selector: wave
        // need to decide touchY and all that here...
        // where to put how to mutate, where to calc offsets
        // who passes it, vm.onSelectedLetter() can be passed to the composable as onSelectLetter()
        // and called from there, that is fine
        // AppSelector package needs to implemented with UI composables for app selector there
        // aka niagara letter, niagara column etc. need to be there
        // now how to impl offset calc needs to be seen
    }
}


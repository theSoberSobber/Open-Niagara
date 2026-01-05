package com.pavit.vanilla.ui.components.AppList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pavit.vanilla.model.AppEntry

@Composable
fun AppList(
    groupedFilteredApps: Map<Char, List<AppEntry>>
){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 120.dp, top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        groupedFilteredApps.forEach { (letter, apps) ->
            // Section header
            item(key = "header_$letter") {
                Text(
                    text = letter.toString(),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            // Apps in this section
            // added to-make-it-unique prefix to ensure it doesn't collide with some other key and crash
            // telling from experience :(
            items(
                items = apps,
                key = { "to-make-it-unique-$it.packageName" }
            ) { app ->
                AppListItem(app = app)
            }
        }
    }
}
package com.pavit.vanilla.ui.components.AppList

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.pavit.vanilla.model.AppEntry

@Composable
fun AppListItem(app: AppEntry) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                try {
                    val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                    intent?.let { context.startActivity(it) }
                } catch (e: Exception) {
                    Log.e("AppLaunch", "Error launching app", e)
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App icon
        Image(
            painter = rememberDrawablePainter(app.icon),
            contentDescription = app.packageName,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))

        // App name
        Text(
            text = app.name,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}
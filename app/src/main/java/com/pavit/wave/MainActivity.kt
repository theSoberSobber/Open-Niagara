package com.pavit.wave

import android.R
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.UserManager
import android.util.Log
import android.view.WindowManager
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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.exp

data class AppInfo(
    val packageName: String,
    val name: String,
    val icon: Drawable?
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContent {
            WaveTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NiagaraPrototype()
                }
            }
        }
    }
}

// Get installed apps - using Kiss launcher approach
@Composable
fun rememberInstalledApps(): List<AppInfo> {
    val context = LocalContext.current
    val apps = remember {
        val pm = context.packageManager
        val appsList = mutableListOf<AppInfo>()
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Android 5.0+ - use LauncherApps (modern way, gets all apps)
                val userManager = context.getSystemService(Context.USER_SERVICE) as? UserManager
                val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
                
                if (userManager != null && launcherApps != null) {
                    for (profile in userManager.getUserProfiles()) {
                        for (activityInfo in launcherApps.getActivityList(null, profile)) {
                            try {
                                val appName = activityInfo.label.toString()
                                val icon = activityInfo.getIcon(0)
                                appsList.add(
                                    AppInfo(
                                        packageName = activityInfo.applicationInfo.packageName,
                                        name = appName,
                                        icon = icon
                                    )
                                )
                            } catch (e: Exception) {
                                Log.e("AppList", "Error loading app from LauncherApps", e)
                            }
                        }
                    }
                }
            } else {
                // Pre-Android 5.0 - use queryIntentActivities with flag 0 (like Kiss)
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }
                
                for (info in pm.queryIntentActivities(intent, 0)) {
                    try {
                        val activityInfo = info.activityInfo
                        val appName = activityInfo.loadLabel(pm).toString()
                        val icon = activityInfo.loadIcon(pm)
                        appsList.add(
                            AppInfo(
                                packageName = activityInfo.packageName,
                                name = appName,
                                icon = icon
                            )
                        )
                    } catch (e: Exception) {
                        Log.e("AppList", "Error loading app from queryIntentActivities", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AppList", "Error getting apps", e)
        }
        
        Log.d("AppList", "Found ${appsList.size} apps")
        
        appsList.distinctBy { it.packageName }
            .sortedBy { it.name.uppercase() }
            .also { Log.d("AppList", "Final app count: ${it.size}") }
    }
    return apps
}

// Get wallpaper bitmap - proper implementation
@Composable
fun rememberWallpaper(): Bitmap? {
    val context = LocalContext.current
    var wallpaper by remember { mutableStateOf<Bitmap?>(null) }
    
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val wallpaperManager = android.app.WallpaperManager.getInstance(context)
                val drawable = wallpaperManager.drawable
                
                if (drawable != null) {
                    // Get screen dimensions for proper sizing
                    val displayMetrics = context.resources.displayMetrics
                    val width = displayMetrics.widthPixels
                    val height = displayMetrics.heightPixels
                    
                    val bitmap = drawable.toBitmap(width, height)
                    Log.d("Wallpaper", "Wallpaper loaded: ${bitmap.width}x${bitmap.height}")
                    wallpaper = bitmap
                } else {
                    Log.w("Wallpaper", "Wallpaper drawable is null")
                }
            } catch (e: Exception) {
                Log.e("Wallpaper", "Error loading wallpaper: ${e.message}", e)
            }
        }
    }
    
    return wallpaper
}

// Get first letter for grouping
fun getFirstLetter(name: String): Char {
    val firstChar = name.uppercase().firstOrNull() ?: return '#'
    return if (firstChar in 'A'..'Z') {
        firstChar
    } else {
        '#'
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
    var columnRightEdge by remember { mutableStateOf(0f) }
    var letterPositions by remember { mutableStateOf<Map<Char, Float>>(emptyMap()) }
    var letterXPositions by remember { mutableStateOf<Map<Char, Float>>(emptyMap()) }
    
    // Get wallpaper and apps
    val wallpaper = rememberWallpaper()
    val allApps = rememberInstalledApps()
    
    // Selected letter for filtering (null = show all)
    var selectedLetter by remember { mutableStateOf<Char?>(null) }
    
    // Group apps by first letter
    val groupedApps = remember(allApps) {
        allApps.groupBy { app -> getFirstLetter(app.name) }
            .toSortedMap()
    }
    
    // Filter apps based on selected letter
    val filteredApps = remember(selectedLetter, groupedApps) {
        if (selectedLetter == null || selectedLetter == '⭐') {
            // Show all apps grouped by letter
            groupedApps
        } else {
            // Show only apps for selected letter
            mapOf(selectedLetter!! to (groupedApps[selectedLetter] ?: emptyList()))
                .filter { it.value.isNotEmpty() }
        }
    }
    
    // Calculate all offsets in the parent
    val letterOffsets = remember(touchY, letterPositions, columnHeight) {
        if (touchY == null || columnHeight == 0f || letterPositions.isEmpty()) {
            alphabet.associateWith { 0f }
        } else {
            alphabet.associateWith { letter ->
                val letterY = letterPositions[letter] ?: 0f
                calculateElasticOffset(touchY!!, letterY, columnHeight)
            }
        }
    }
    
    // Find the maximum offset and which letter has it
    val (maxOffset, letterAtPeak) = remember(letterOffsets) {
        if (letterOffsets.isEmpty()) {
            Pair(0f, null)
        } else {
            val max = letterOffsets.values.maxOrNull() ?: 0f
            val letter = letterOffsets.entries.find { it.value == max }?.key
            Pair(max, letter)
        }
    }
    
    // Update selected letter when letterAtPeak changes, or clear when touch is released
    LaunchedEffect(letterAtPeak, touchY) {
        if (touchY == null) {
            selectedLetter = null
        } else if (letterAtPeak != null && letterAtPeak != '⭐') {
            selectedLetter = letterAtPeak
        }
    }
    
    // Get the actual X position of the letter at peak
    val peakLetterX = remember(letterAtPeak, letterXPositions) {
        if (letterAtPeak != null) {
            letterXPositions[letterAtPeak] ?: 0f
        } else {
            0f
        }
    }
    
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Wallpaper background
        wallpaper?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Wallpaper",
                modifier = Modifier.fillMaxSize()
            )
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )
        
        // Dim overlay to make content readable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )
        
        // App list with section headers
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 120.dp, top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            filteredApps.forEach { (letter, apps) ->
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
                items(
                    items = apps,
                    key = { it.packageName }
                ) { app ->
                    AppListItem(app = app)
                }
            }
        }

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
                    val rect = coordinates.positionInRoot()
                    columnRightEdge = rect.x + coordinates.size.width
                },
            verticalArrangement = Arrangement.spacedBy(0.001.dp),
            horizontalAlignment = Alignment.End
        ) {
            alphabet.forEach { letter ->
                NiagaraLetter(
                    letter = letter,
                    offset = letterOffsets[letter] ?: 0f,
                    onPositionUpdate = { x, y ->
                        letterPositions = letterPositions + (letter to y)
                        letterXPositions = letterXPositions + (letter to x)
                    }
                )
            }
        }

        // Draw circle at max offset position
        // Use actual letter X position (peakLetterX) which was working!
        // X = letter center - full circle diameter - padding (to the left, no overlap)
        // Y follows touchY
        if (touchY != null && maxOffset > 0f && peakLetterX > 0f && letterAtPeak != null) {
            val circleSize = 48.dp // Bigger circle
            val circleRadiusPx = with(density) { (circleSize / 2f).toPx() }
            val letterPaddingPx = with(density) { 32.dp.toPx() }
            
            // Move circle further left: letter center - radius - padding to avoid overlap
            val circleCenterX = peakLetterX - circleRadiusPx - letterPaddingPx
            
            Box(
                modifier = Modifier
                    .offset(
                        x = with(density) { circleCenterX.toDp() },
                        y = with(density) { (touchY!! - circleRadiusPx).toDp() }
                    )
                    .size(circleSize)
                    .clip(CircleShape)
                    .background(Color(0xFF6200EE)), // Purple/Material color
                contentAlignment = Alignment.Center
            ) {
                // Letter inside the circle
                Text(
                    text = letterAtPeak.toString(),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Debug: Show a red line where the finger is
        if (touchY != null) {
            Box(
                modifier = Modifier
                    .offset(y = with(density) { touchY!!.toDp() })
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
    offset: Float,
    onPositionUpdate: (Float, Float) -> Unit
) {
    // Animate the offset with spring physics for elastic feel
    val animatedOffset by animateFloatAsState(
        targetValue = offset,
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
                val letterCenterX = rect.x + (coordinates.size.width / 2f)
                val letterCenterY = rect.y + (coordinates.size.height / 2f)
                onPositionUpdate(letterCenterX, letterCenterY)
            }
    )
}

@Composable
fun AppListItem(app: AppInfo) {
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
        app.icon?.let { drawable ->
            val bitmap = remember(drawable) {
                try {
                    if (drawable is BitmapDrawable) {
                        drawable.bitmap
                    } else {
                        drawable.toBitmap(64, 64)
                    }
                } catch (e: Exception) {
                    null
                }
            }
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = app.name,
                    modifier = Modifier.size(48.dp)
                )
            } ?: Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Gray.copy(alpha = 0.3f))
            )
        } ?: Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.Gray.copy(alpha = 0.3f))
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

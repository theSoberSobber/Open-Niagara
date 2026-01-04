package com.pavit.vanilla.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.pavit.vanilla.model.AppEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AppRepository {
    @SuppressLint("QueryPermissionsNeeded")
    override suspend fun getInstalledApps(): List<AppEntry> {
        return withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            // query intent activities or similar no longe return all apps, we need QUERY_ALL_PACKAGES perm
            // for that, otherwise <queries> filter is required
            // https://g.co/dev/packagevisibility, this is what the supress is about
            pm.queryIntentActivities(intent, 0).map { info ->
                AppEntry(
                    name = info.loadLabel(pm).toString(),
                    packageName = info.activityInfo.packageName,
                    icon = info.loadIcon(pm)
                )
            }.sortedBy { it.name.lowercase() }
        }
    }
}
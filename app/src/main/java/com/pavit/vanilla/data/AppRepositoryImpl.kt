package com.pavit.vanilla.data

import android.content.Context
import com.pavit.vanilla.model.AppEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AppRepository {
    override suspend fun getInstalledApps(): List<AppEntry> {
        TODO("Not yet implemented")
    }
}
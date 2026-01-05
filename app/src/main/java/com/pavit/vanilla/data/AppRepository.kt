package com.pavit.vanilla.data

import com.pavit.vanilla.model.AppEntry
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    suspend fun getInstalledApps(): List<AppEntry>
    fun getInstalledAppsStream(): Flow<List<AppEntry>>
}
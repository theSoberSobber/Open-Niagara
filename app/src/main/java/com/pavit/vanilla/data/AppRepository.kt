package com.pavit.vanilla.data

import com.pavit.vanilla.model.AppEntry

interface AppRepository {
    suspend fun getInstalledApps(): List<AppEntry>
}
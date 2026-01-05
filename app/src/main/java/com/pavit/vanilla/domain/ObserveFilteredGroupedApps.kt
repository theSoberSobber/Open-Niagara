package com.pavit.vanilla.domain

import androidx.compose.runtime.remember
import com.pavit.vanilla.data.AppRepository
import com.pavit.vanilla.model.AppEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.isNotEmpty

// needs to return a flow to be observable
class ObserveFilteredGroupedApps @Inject constructor(
    private val repo: AppRepository
) {
    private fun getFirstLetter(name: String): Char {
        val firstChar = name.uppercase().firstOrNull() ?: return '#'
        return if (firstChar in 'A'..'Z') {
            firstChar
        } else {
            '#'
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        selectedLetter: Flow<Char?>
    ): Flow<Map<Char, List<AppEntry>>> {
        // flatMapLatest ensures a new flow is created for each selectedLetter passed
        // the prev ones are disposed off nicely
        return selectedLetter.flatMapLatest {
            selectedLetterCurrent -> repo.getInstalledAppsStream()
            .map { appEntryList ->
                // group apps by first letter
                val groupedMap = appEntryList
                    .groupBy { app -> getFirstLetter(app.name) }
                    .toSortedMap()

                // filter, for now favourites can just be all letters
                if (selectedLetterCurrent == null || selectedLetterCurrent == '⭐') {
                    groupedMap
                } else {
                    val filteredList = groupedMap[selectedLetterCurrent] ?: emptyList()
                    if (filteredList.isNotEmpty()) {
                        mapOf(selectedLetterCurrent to filteredList)
                    } else {
                        emptyMap()
                    }
                }
            }
        }
    }
}
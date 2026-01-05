package com.pavit.vanilla.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pavit.vanilla.data.AppRepository
import com.pavit.vanilla.domain.ObserveFilteredGroupedApps
import com.pavit.vanilla.model.AppEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// state owner
// to prevent against rotation and dark/light mode etc. state changes
// that result in activity destroy and hence loss of state completely

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val repo: AppRepository,
    observeFilteredGroupedApps: ObserveFilteredGroupedApps
): ViewModel() {
    // define a flow for apps here and get the apps here
    // in view model init
    // view model's life cycle will surpass that of activity

//    private val _apps = MutableStateFlow<List<AppEntry>>(emptyList())
//    val apps: StateFlow<List<AppEntry>> =  _apps;

    // newer impl
    private val _selectedLetter = MutableStateFlow<Char?>(null)
    val selectedLetter: StateFlow<Char?> = _selectedLetter

    val groupedFilteredApps: StateFlow<Map<Char, List<AppEntry>>> = observeFilteredGroupedApps(selectedLetter)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5_000),
            initialValue = emptyMap()
        )

    // launch in view model scope because it won't get cancelled
    // if somehow the activity/compose gets weirded out/destroyed

//    init {
//        viewModelScope.launch {
//            _apps.value = repo.getInstalledApps()
//        }
//    }

    fun onLetterSelected(letter: Char?){
        _selectedLetter.value = letter
    }
}
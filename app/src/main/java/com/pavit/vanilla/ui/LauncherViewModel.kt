package com.pavit.vanilla.ui

import androidx.lifecycle.ViewModel
import com.pavit.vanilla.data.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// state owner
// to prevent against rotation and dark/light mode etc. state changes
// that result in activity destroy and hence loss of state completely

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val repo: AppRepository
): ViewModel() {
    // define a flow for apps here and get the apps here
    // in view model init
    // view model's life cycle will surpass that of activity
}
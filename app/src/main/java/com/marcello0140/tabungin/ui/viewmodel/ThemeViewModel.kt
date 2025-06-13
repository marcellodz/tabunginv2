package com.marcello0140.tabungin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcello0140.tabungin.datastore.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(private val preferenceManager: PreferenceManager) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    val isDarkMode: StateFlow<Boolean> = preferenceManager.darkModeFlow
        .onEach { _isLoading.value = false } // saat sudah load → false
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleTheme() {
        viewModelScope.launch {
            preferenceManager.setDarkMode(!isDarkMode.value)
        }
    }
}

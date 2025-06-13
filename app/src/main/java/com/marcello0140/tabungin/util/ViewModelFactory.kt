package com.marcello0140.tabungin.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.marcello0140.tabungin.data.WishListRepository
import com.marcello0140.tabungin.datastore.PreferenceManager
import com.marcello0140.tabungin.ui.viewmodel.DetailViewModel
import com.marcello0140.tabungin.ui.viewmodel.MainViewModel
import com.marcello0140.tabungin.ui.viewmodel.ThemeViewModel

class ViewModelFactory(
    private val repository: WishListRepository,
    private val preferenceManager: PreferenceManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ThemeViewModel::class.java) -> {
                ThemeViewModel(preferenceManager) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

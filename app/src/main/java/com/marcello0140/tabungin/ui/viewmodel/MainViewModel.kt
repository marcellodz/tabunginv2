package com.marcello0140.tabungin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcello0140.tabungin.data.WishListRepository
import com.marcello0140.tabungin.model.WishListWithHistory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(
    private val repository: WishListRepository
) : ViewModel() {

    val wishListWithHistory: StateFlow<List<WishListWithHistory>> =
        repository.getAllWishLists()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun addWishlist(name: String, targetAmount: Int, imageUrl: String?): Long {
        val createdAt = getTodayDate()
        return repository.addWishList(name, targetAmount, createdAt, imageUrl)
    }

    private fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}

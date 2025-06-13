package com.marcello0140.tabungin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcello0140.tabungin.data.WishListRepository
import com.marcello0140.tabungin.model.TabunganHistory
import com.marcello0140.tabungin.model.WishList
import com.marcello0140.tabungin.model.WishListWithHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DetailViewModel(
    private val repository: WishListRepository
) : ViewModel() {

    private val _wishListWithHistory = MutableStateFlow<WishListWithHistory?>(null)
    val wishListWithHistory: StateFlow<WishListWithHistory?> = _wishListWithHistory

    private var currentId: Long? = null

    fun loadWishListById(id: Long) {
        currentId = id
        viewModelScope.launch {
            repository.getWishListByIdFlow(id).collectLatest { result ->
                _wishListWithHistory.value = result
            }
        }
    }

    private fun refreshCurrent() {
        viewModelScope.launch {
            currentId?.let { id ->
                val latest = repository.getWishListByIdOnce(id)
                _wishListWithHistory.value = latest
            }
        }
    }

    fun addHistoryItem(wishListId: Long, nominal: Int, isPenambahan: Boolean) {
        viewModelScope.launch {
            repository.addHistory(wishListId, nominal, isPenambahan, getTodayDate())
            refreshCurrent()
        }
    }

    fun editHistoryItem(history: TabunganHistory, newNominal: Int, isPenambahan: Boolean) {
        viewModelScope.launch {
            repository.updateHistory(history.copy(nominal = newNominal, isPenambahan = isPenambahan))
            refreshCurrent()
        }
    }

    fun deleteHistoryItem(history: TabunganHistory) {
        viewModelScope.launch {
            repository.deleteHistory(history)
            refreshCurrent()
        }
    }

    fun updateWishlist(updated: WishList) {
        viewModelScope.launch {
            repository.updateWishList(updated)
            refreshCurrent()
        }
    }

    fun deleteWishlist(wishList: WishList) {
        viewModelScope.launch {
            repository.deleteWishList(wishList)
            // Tidak perlu refresh karena akan langsung navigate back
        }
    }

    private fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }
}

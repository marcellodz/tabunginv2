package com.marcello0140.tabungin.data

import com.marcello0140.tabungin.database.HistoryDao
import com.marcello0140.tabungin.database.WishListDao
import com.marcello0140.tabungin.model.TabunganHistory
import com.marcello0140.tabungin.model.WishList
import com.marcello0140.tabungin.model.WishListWithHistory
import kotlinx.coroutines.flow.Flow

class WishListRepository(
    private val wishListDao: WishListDao,
    private val historyDao: HistoryDao
) {

    fun getAllWishLists(): Flow<List<WishListWithHistory>> =
        wishListDao.getAllWishListsWithHistory()

    fun getWishListByIdFlow(id: Long): Flow<WishListWithHistory?> =
        wishListDao.getWishListWithHistoryFlow(id)

    suspend fun getWishListByIdOnce(id: Long): WishListWithHistory? =
        wishListDao.getWishListWithHistoryOnce(id)

    suspend fun addWishList(name: String, targetAmount: Int, createdAt: String, imageUrl: String?): Long {
        val wishList = WishList(
            name = name,
            targetAmount = targetAmount,
            createdAt = createdAt,
            imageUrl = imageUrl
        )
        return wishListDao.insertWishList(wishList)
    }

    suspend fun addHistory(wishListId: Long, nominal: Int, isPenambahan: Boolean, tanggal: String) {
        val history = TabunganHistory(
            wishListId = wishListId,
            nominal = nominal,
            isPenambahan = isPenambahan,
            tanggal = tanggal
        )
        historyDao.insertHistory(history)
    }

    suspend fun updateWishList(wishList: WishList) {
        wishListDao.updateWishList(wishList)
    }

    suspend fun deleteWishList(wishList: WishList) {
        wishListDao.deleteWishList(wishList)
    }

    suspend fun updateHistory(history: TabunganHistory) {
        historyDao.updateHistory(history)
    }

    suspend fun deleteHistory(history: TabunganHistory) {
        historyDao.deleteHistory(history)
    }
}


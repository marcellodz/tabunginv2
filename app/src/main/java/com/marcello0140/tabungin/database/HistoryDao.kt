package com.marcello0140.tabungin.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.marcello0140.tabungin.model.TabunganHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM riwayat_tabungan WHERE wishListId = :wishListId")
    fun getHistoriesByWishListId(wishListId: Long): Flow<List<TabunganHistory>>

    @Insert
    suspend fun insertHistory(history: TabunganHistory): Long

    @Update
    suspend fun updateHistory(history: TabunganHistory)

    @Delete
    suspend fun deleteHistory(history: TabunganHistory)
}

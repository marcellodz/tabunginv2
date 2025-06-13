package com.marcello0140.tabungin.database


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.marcello0140.tabungin.model.WishList
import com.marcello0140.tabungin.model.WishListWithHistory
import kotlinx.coroutines.flow.Flow


@Dao
interface WishListDao {
    @Transaction
    @Query("SELECT * FROM tabungin2_db")
    fun getAllWishListsWithHistory(): Flow<List<WishListWithHistory>>

    @Transaction
    @Query("SELECT * FROM tabungin2_db WHERE id = :id")
    fun getWishListWithHistoryFlow(id: Long): Flow<WishListWithHistory?>

    @Transaction
    @Query("SELECT * FROM tabungin2_db WHERE id = :id")
    suspend fun getWishListWithHistoryOnce(id: Long): WishListWithHistory?


    @Transaction
    @Query("SELECT * FROM tabungin2_db WHERE id = :id")
    fun getWishListWithHistoryById(id: Long): Flow<WishListWithHistory?>

    @Insert
    suspend fun insertWishList(wishList: WishList): Long

    @Update
    suspend fun updateWishList(wishList: WishList)

    @Delete
    suspend fun deleteWishList(wishList: WishList)
}

package com.marcello0140.tabungin.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.marcello0140.tabungin.model.TabunganHistory
import com.marcello0140.tabungin.model.WishList

@Database(entities = [WishList::class, TabunganHistory::class], version = 1)
abstract class TabungInDb : RoomDatabase() {
    abstract fun wishListDao(): WishListDao
    abstract fun historyDao(): HistoryDao
}

object DatabaseInstance {
    @Volatile
    private var INSTANCE: TabungInDb? = null

    fun getDatabase(context: Context): TabungInDb {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                TabungInDb::class.java,
                "tabungin2_db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}

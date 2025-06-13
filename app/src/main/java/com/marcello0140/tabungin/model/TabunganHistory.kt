package com.marcello0140.tabungin.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "riwayat_tabungan",
    foreignKeys = [
        ForeignKey(
            entity = WishList::class,
            parentColumns = ["id"],
            childColumns = ["wishListId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TabunganHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val wishListId: Long,
    val tanggal: String,
    val nominal: Int,
    val isPenambahan: Boolean
)


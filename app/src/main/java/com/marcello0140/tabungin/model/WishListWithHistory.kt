package com.marcello0140.tabungin.model

import androidx.room.Embedded
import androidx.room.Relation

data class WishListWithHistory(
    @Embedded val wishList: WishList,
    @Relation(
        parentColumn = "id",
        entityColumn = "wishListId"
    )
    val histories: List<TabunganHistory>
)

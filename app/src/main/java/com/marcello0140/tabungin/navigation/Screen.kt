package com.marcello0140.tabungin.navigation

sealed class Screen(val route: String) {
    data object Main : Screen("main_screen")
    data object Detail : Screen("detail_screen/{id}") {
        fun navigationWithId(id: Int) = "detail_screen/$id"
    }
}

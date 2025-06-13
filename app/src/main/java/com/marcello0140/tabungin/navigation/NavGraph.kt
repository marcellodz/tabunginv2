package com.marcello0140.tabungin.navigation

import com.marcello0140.tabungin.ui.screen.DetailScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.marcello0140.tabungin.data.WishListRepository
import com.marcello0140.tabungin.datastore.PreferenceManager
import com.marcello0140.tabungin.ui.screen.MainScreen
import com.marcello0140.tabungin.ui.viewmodel.DetailViewModel
import com.marcello0140.tabungin.ui.viewmodel.MainViewModel
import com.marcello0140.tabungin.ui.viewmodel.ThemeViewModel
import com.marcello0140.tabungin.util.ViewModelFactory

@Composable
fun NavGraph(
    navController: NavHostController,
    repository: WishListRepository,
    preferenceManager: PreferenceManager
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            val mainViewModel: MainViewModel = viewModel(factory = ViewModelFactory(repository, preferenceManager))
            val themeViewModel: ThemeViewModel = viewModel(factory = ViewModelFactory(repository, preferenceManager))

            MainScreen(
                navController = navController,
                viewModel = mainViewModel,
                themeViewModel = themeViewModel
            )
        }


        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable

            val detailViewModel: DetailViewModel = viewModel(factory = ViewModelFactory(repository, preferenceManager))

            LaunchedEffect(id) {
                detailViewModel.loadWishListById(id)
            }

            DetailScreen(
                viewModel = detailViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

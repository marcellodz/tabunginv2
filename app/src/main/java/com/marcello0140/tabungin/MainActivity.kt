package com.marcello0140.tabungin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.marcello0140.tabungin.data.WishListRepository
import com.marcello0140.tabungin.database.DatabaseInstance
import com.marcello0140.tabungin.datastore.PreferenceManager
import com.marcello0140.tabungin.navigation.NavGraph
import com.marcello0140.tabungin.ui.theme.TabungInTheme
import com.marcello0140.tabungin.ui.viewmodel.ThemeViewModel
import com.marcello0140.tabungin.util.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = WishListRepository(
            DatabaseInstance.getDatabase(this).wishListDao(),
            DatabaseInstance.getDatabase(this).historyDao()
        )
        val preferenceManager = PreferenceManager(this)

        setContent {
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ViewModelFactory(repository, preferenceManager)
            )
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()
            val isLoading by themeViewModel.isLoading.collectAsState()

            if (isLoading) {
                // Splash screen atau blank sementara loading
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                TabungInTheme(darkTheme = isDarkMode) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        NavGraph(
                            navController = navController,
                            repository = repository,
                            preferenceManager = preferenceManager
                        )
                    }
                }
            }
        }
    }
}

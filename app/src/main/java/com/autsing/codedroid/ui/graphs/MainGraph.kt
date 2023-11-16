package com.autsing.codedroid.ui.graphs

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.autsing.codedroid.ui.screens.CodeScreen
import com.autsing.codedroid.ui.screens.ConfigScreen
import com.autsing.codedroid.ui.viewmodels.MainViewModel

enum class MainGraphDestinations(
    val route: String,
) {
    Config(route = "/config"),
    Code(route = "/code"),
}

@Composable
fun MainGraph(
    mainViewModel: MainViewModel = viewModel(),
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainGraphDestinations.Code.route,
    ) {
        composable(MainGraphDestinations.Config.route) {
            ConfigScreen()
        }
        composable(MainGraphDestinations.Code.route) {
            CodeScreen(mainViewModel)
        }
    }
}
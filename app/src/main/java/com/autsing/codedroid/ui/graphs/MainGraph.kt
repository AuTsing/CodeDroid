package com.autsing.codedroid.ui.graphs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.autsing.codedroid.ui.screens.CodeScreen
import com.autsing.codedroid.ui.screens.ConfigScreen
import com.autsing.codedroid.ui.theme.CodeDroidTheme
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
    val destination by mainViewModel.destination.collectAsState()

    LaunchedEffect(destination) {
        if (navController.currentDestination?.route != destination.route) {
            navController.navigate(destination.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        }
    }
    LaunchedEffect(Unit) {
        mainViewModel.handleGotoCode()
    }

    CodeDroidTheme {
        NavHost(
            navController = navController,
            startDestination = MainGraphDestinations.Config.route,
        ) {
            composable(MainGraphDestinations.Config.route) {
                ConfigScreen(mainViewModel)
            }
            composable(MainGraphDestinations.Code.route) {
                CodeScreen(mainViewModel)
            }
        }
    }
}

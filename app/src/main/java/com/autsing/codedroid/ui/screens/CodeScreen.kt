package com.autsing.codedroid.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.autsing.codedroid.ui.viewmodels.MainViewModel

@Composable
fun CodeScreen(
    vm: MainViewModel = viewModel(),
) {
    val uiState = vm.uiState

//    if (uiState.loading) {
//        LoadingScreen()
//    } else {
    CodeContent()
//    }
}

@Composable
fun CodeContent() {
}

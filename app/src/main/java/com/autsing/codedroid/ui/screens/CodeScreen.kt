package com.autsing.codedroid.ui.screens

import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.autsing.codedroid.ui.viewmodels.MainViewModel

@Composable
fun CodeScreen(
    vm: MainViewModel = viewModel(),
) {
    val uiState = vm.uiState

    if (uiState.loading) {
        LoadingScreen()
    } else {
        CodeContent(
            webView = vm.getWebView(),
        )
    }
}

@Composable
fun CodeContent(webView: WebView) {
    AndroidView(
        factory = { webView },
        modifier = Modifier.fillMaxSize(),
    )
}

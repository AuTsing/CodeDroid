package com.autsing.codedroid.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

const val DEFAULT_IP = "localhost"
const val DEFAULT_PORT = "8080"

data class MainUiState(
    val loading: Boolean = true,
    val ip: String = DEFAULT_IP,
    val port: String = DEFAULT_PORT,
    val maybeException: String? = null,
)

class MainViewModel : ViewModel() {

    var uiState by mutableStateOf(MainUiState())
        private set

    fun handleChangeIp(ip: String) {
        uiState = uiState.copy(ip = ip)
    }

    fun handleChangePort(port: String) {
        uiState = uiState.copy(port = port)
    }

    fun handleGotoCode() {
        uiState = uiState.copy(loading = true)

    }
}
package com.autsing.codedroid.ui.viewmodels

import android.webkit.WebView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.autsing.codedroid.ui.graphs.MainGraphDestinations
import com.autsing.codedroid.utils.WebViewer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val DEFAULT_PROTOCOL = "http"
const val DEFAULT_IP = "127.0.0.1"
const val DEFAULT_PORT = "8080"

data class MainUiState(
    val loading: Boolean = true,
    val ip: String = DEFAULT_IP,
    val port: String = DEFAULT_PORT,
    val maybeException: String? = null,
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val webViewer: WebViewer,
    private val navController: NavHostController,
) : ViewModel() {

    var uiState by mutableStateOf(MainUiState())
        private set

    fun getMaybeWebView(): WebView? {
        return webViewer.maybeWebView
    }

    fun getNavController(): NavHostController {
        return navController
    }

    fun handleChangeIp(ip: String) {
        uiState = uiState.copy(ip = ip)
    }

    fun handleChangePort(port: String) {
        uiState = uiState.copy(port = port)
    }

    fun handleGotoCode() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(loading = true)
                navController.navigate(MainGraphDestinations.Code.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
                val url = "$DEFAULT_PROTOCOL://${uiState.ip}:${uiState.port}/"
                webViewer.open(url).getOrThrow()
                uiState = uiState.copy(loading = false)
            } catch (e: Exception) {
                uiState = uiState.copy(maybeException = e.message)
                navController.navigate(MainGraphDestinations.Config.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            }
        }
    }
}

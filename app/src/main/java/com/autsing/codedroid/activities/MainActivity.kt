package com.autsing.codedroid.activities

import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.autsing.codedroid.ui.graphs.MainGraph
import com.autsing.codedroid.ui.theme.CodeDroidTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val TAG = "codedroid"

class MainActivity : ComponentActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this).apply {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.d(TAG, "onPageFinished: $url")
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    Log.d(TAG, "onReceivedError: $error")
                }
            }

            loadUrl("http://localhost:8080/")
        }

        lifecycleScope.launch {
            delay(3000)
            webView.loadUrl("http://localhost:8080/")
        }

        setContent {
            CodeDroidTheme {
                MainGraph()
            }
        }
    }
}

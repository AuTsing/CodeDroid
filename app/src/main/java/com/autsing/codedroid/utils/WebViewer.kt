package com.autsing.codedroid.utils

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class WebViewer @Inject constructor() {
    private val coroutineScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
    var maybeWebView: WebView? = null

    init {
        WebView.setWebContentsDebuggingEnabled(true)
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun initWebView(context: Context) {
        maybeWebView = WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
            settings.loadsImagesAutomatically = true
            settings.defaultTextEncodingName = "utf-8"
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.builtInZoomControls = false
            settings.setSupportMultipleWindows(true)
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            settings.pluginState = WebSettings.PluginState.ON
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            settings.safeBrowsingEnabled = true
            settings.mediaPlaybackRequiresUserGesture = true
            settings.blockNetworkImage = true
            settings.setGeolocationEnabled(true)
        }
    }

    fun clearWebView() {
        maybeWebView = null
    }

    suspend fun open(url: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        if (maybeWebView == null) {
            continuation.resume(Result.failure(Exception("WebView not initialized")))
            return@suspendCancellableCoroutine
        }
        val webView = maybeWebView!!
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (continuation.isActive) {
                    continuation.resume(Result.success(Unit))
                }
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError,
            ) {
                super.onReceivedError(view, request, error)
                if (continuation.isActive) {
                    continuation.resume(Result.failure(Exception(error.description.toString())))
                }
            }
        }
        webView.loadUrl(url)
    }

    suspend fun openWithMinDelay(url: String): Result<Unit> {
        val minDelay = coroutineScope.launch { delay(1000) }
        val result = open(url)
        minDelay.join()
        return result
    }
}

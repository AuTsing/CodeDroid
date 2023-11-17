package com.autsing.codedroid.utils

import android.content.Context
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import dagger.hilt.android.qualifiers.ApplicationContext
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
class WebViewer @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) {
    private val coroutineScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
    val webView: WebView = WebView(applicationContext).apply {
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.allowFileAccess = true
        settings.allowContentAccess = true
        settings.loadsImagesAutomatically = true
        settings.defaultTextEncodingName = "utf-8"
        settings.domStorageEnabled = true
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.builtInZoomControls = false
    }

    suspend fun open(url: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
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

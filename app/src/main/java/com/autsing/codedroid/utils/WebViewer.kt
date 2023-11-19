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

    @SuppressLint("SetJavaScriptEnabled")
    fun initWebView(context: Context) {
        maybeWebView = WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.useWideViewPort = true
            settings.allowFileAccess = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.loadWithOverviewMode = true
            settings.defaultTextEncodingName = "utf-8"
            settings.loadsImagesAutomatically = true
            settings.setSupportMultipleWindows(true)
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
//            settings.allowUniversalAccessFromFileURLs = true
//            webChromeClient = object : WebChromeClient() {
//                override fun onCreateWindow(
//                    view: WebView?,
//                    isDialog: Boolean,
//                    isUserGesture: Boolean,
//                    resultMsg: Message?
//                ): Boolean {
//                    Log.d(TAG, "onCreateWindow: ")
//                    return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
//                }
//            }
        }
    }

    fun clearWebView() {
        maybeWebView?.removeAllViews()
        maybeWebView?.destroy()
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

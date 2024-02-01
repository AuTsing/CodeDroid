package com.autsing.codedroid.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import androidx.webkit.WebResourceErrorCompat
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewFeature
import com.autsing.codedroid.activities.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WebViewer @Inject constructor() {
    private val coroutineScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
    private val assetLoader: WebViewAssetLoader = WebViewAssetLoader.Builder().build()
    var maybeWebView: WebView? = null
    var loaded: Boolean = false
    var maybeException: Exception? = null

    @SuppressLint("SetJavaScriptEnabled")
    fun initWebView(context: Context) {
        maybeWebView = WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.useWideViewPort = true
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.loadWithOverviewMode = true
            settings.defaultTextEncodingName = "utf-8"
            settings.loadsImagesAutomatically = true
            settings.setSupportMultipleWindows(true)
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            webViewClient = object : WebViewClientCompat() {
                override fun shouldInterceptRequest(
                    view: WebView,
                    request: WebResourceRequest,
                ): WebResourceResponse? {
                    return assetLoader.shouldInterceptRequest(request.url)
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest,
                ): Boolean {
                    return false
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    loaded = true
                }

                override fun onReceivedError(
                    view: WebView,
                    request: WebResourceRequest,
                    error: WebResourceErrorCompat,
                ) {
                    super.onReceivedError(view, request, error)
                    maybeException = if (
                        WebViewFeature.isFeatureSupported(WebViewFeature.WEB_RESOURCE_ERROR_GET_DESCRIPTION)
                    ) {
                        Exception(error.description.toString())
                    } else if (
                        WebViewFeature.isFeatureSupported(WebViewFeature.WEB_RESOURCE_ERROR_GET_CODE)
                    ) {
                        Exception(error.errorCode.toString())
                    } else {
                        Exception(error.toString())
                    }
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onShowFileChooser(
                    webView: WebView,
                    filePathCallback: ValueCallback<Array<Uri>>,
                    fileChooserParams: FileChooserParams,
                ): Boolean {
                    coroutineScope.launch {
                        try {
                            val intent = fileChooserParams.createIntent()
                            val channel = MainActivity.requestFileChosen(intent).getOrThrow()
                            val uris = channel.receive().getOrThrow()
                            filePathCallback.onReceiveValue(uris)
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    MainActivity.instance.get(),
                                    "打开文件选择器失败: ${e.message}",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                            filePathCallback.onReceiveValue(null)
                        }
                    }
                    return true
                }
            }

            setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                Toast.makeText(context, "暂不支持下载: $url", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun clearWebView() {
        loaded = false
        maybeException = null
        maybeWebView?.removeAllViews()
        maybeWebView?.destroy()
        maybeWebView = null
    }

    suspend fun open(url: String): Result<Unit> = runCatching {
        maybeWebView?.loadUrl(url)
        withTimeout(10000) {
            while (true) {
                delay(1000)
                if (maybeException != null) {
                    throw maybeException as Exception
                }
                if (loaded) {
                    return@withTimeout
                }
            }
        }
    }
}

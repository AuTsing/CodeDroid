package com.autsing.codedroid

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.autsing.codedroid.MainActivity.Companion.TAG
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient

class WebActivity : AppCompatActivity() {
    companion object {
        var maybeException: Exception? = null
    }

    private lateinit var layout: ConstraintLayout
    private lateinit var agentWeb: AgentWeb
    private val webChromeClient: WebChromeClient = object : WebChromeClient() {}
    private val webViewClient: WebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?,
        ): Boolean {
            return false
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError,
        ) {
            maybeException = Exception("Error code: ${error.errorCode}, ${error.description}")
            this@WebActivity.finish()
        }
    }

    private fun getUrl(): String {
        return "http://google.com"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_web)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        layout = findViewById(R.id.container)
        agentWeb = AgentWeb.with(this)
            .setAgentWebParent(layout, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .setWebChromeClient(webChromeClient)
            .setWebViewClient(webViewClient)
            .setMainFrameErrorView(com.just.agentweb.R.layout.agentweb_error_page, -1)
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
            .interceptUnkownUrl()
            .createAgentWeb()
            .ready()
            .go(getUrl());
    }
}

package com.autsing.codedroid

import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient

class WebActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_KEY_IP = "ip"
        const val EXTRA_KEY_PORT = "port"
        var maybeException: Exception? = null
    }

    private var url: String = ""
    private var tryFinishAt: Long = 0
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_web)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val ip = intent.getStringExtra(EXTRA_KEY_IP)
        val port = intent.getStringExtra(EXTRA_KEY_PORT)
        url = "${MainActivity.DEFAULT_PROTOCOL}://$ip:$port"

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
            .go(url);
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val now = System.currentTimeMillis()
            if (now - tryFinishAt < 2000) {
                finish()
                return true
            }
            Toast.makeText(this, R.string.text_activity_finish_confirmation, Toast.LENGTH_SHORT)
                .show()
            tryFinishAt = now
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
}

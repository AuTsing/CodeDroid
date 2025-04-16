package com.autsing.codedroid

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.ValueCallback
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class WebActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KEY_URL = "url"

        var maybeException: String? = null

        fun startActivity(context: Context, url: String) {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra(EXTRA_KEY_URL, url)
            context.startActivity(intent)
        }
    }

    private var url: String = ""
    private var tryFinishAt: Long = 0
    private val fileChooserChannel = Channel<Result<Array<Uri>>>()
    private val fileChooserLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {
        when (it.resultCode) {
            RESULT_OK -> {
                val uris = mutableListOf<Uri>()
                it.data?.dataString?.let { ds ->
                    val uri = ds.toUri()
                    uris.add(uri)
                }
                it.data?.clipData?.let { cds ->
                    for (i in 0..cds.itemCount) {
                        val cd = cds.getItemAt(i)
                        uris.add(cd.uri)
                    }
                }
                fileChooserChannel.trySend(Result.success(uris.toTypedArray()))
            }

            RESULT_CANCELED -> fileChooserChannel.trySend(Result.success(emptyArray()))

            RESULT_FIRST_USER -> fileChooserChannel.trySend(Result.failure(Exception("RESULT_FIRST_USER")))
        }
    }
    private lateinit var layout: ConstraintLayout
    private lateinit var agentWeb: AgentWeb
    private val webChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onShowFileChooser(
            view: WebView,
            cb: ValueCallback<Array<Uri>>,
            params: FileChooserParams,
        ): Boolean {
            lifecycleScope.launch {
                runCatching {
                    val intent = params.createIntent()
                    fileChooserLauncher.launch(intent)
                    val uris = fileChooserChannel.receive().getOrThrow()
                    cb.onReceiveValue(uris)
                }.onFailure {
                    Toast.makeText(
                        this@WebActivity,
                        "打开文件选择器失败: ${it.message}",
                        Toast.LENGTH_SHORT,
                    ).show()
                    cb.onReceiveValue(null)
                }
            }
            return true
        }
    }
    private val webViewClient: WebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest,
        ): Boolean {
            return false
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError,
        ) {
            maybeException = "Error code: ${error.errorCode}, ${error.description}"
            if (error.errorCode == -6) {
                finish()
            }
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

        window.statusBarColor = getColor(R.color.vscode_dark_1)
        window.navigationBarColor = getColor(R.color.vscode_dark_2)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            false

        url = intent.getStringExtra(EXTRA_KEY_URL) ?: ""

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

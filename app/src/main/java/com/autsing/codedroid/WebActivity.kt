package com.autsing.codedroid

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
import androidx.core.view.ViewCompat
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
        const val EXTRA_KEY_IP = "ip"
        const val EXTRA_KEY_PORT = "port"
        var maybeException: Exception? = null
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
                    val uri = Uri.parse(ds)
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
                try {
                    val intent = params.createIntent()
                    fileChooserLauncher.launch(intent)
                    val uris = fileChooserChannel.receive().getOrThrow()
                    cb.onReceiveValue(uris)
                } catch (e: Exception) {
                    Toast.makeText(
                        this@WebActivity,
                        "打开文件选择器失败: ${e.message}",
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

        override fun onPageFinished(viwe: WebView, url: String) {
            maybeException = null
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

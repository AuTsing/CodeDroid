package com.autsing.codedroid.activities

import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.autsing.codedroid.R
import com.autsing.codedroid.ui.graphs.MainGraph
import com.autsing.codedroid.ui.theme.CodeDroidTheme
import com.autsing.codedroid.ui.viewmodels.MainViewModel
import com.autsing.codedroid.utils.WebViewer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var webViewer: WebViewer

    private val mainViewModel: MainViewModel by viewModels()

    private var tryFinishAt: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webViewer.initWebView(this)

        setContent {
            CodeDroidTheme {
                MainGraph()
            }
        }
    }

    override fun onDestroy() {
        webViewer.clearWebView()
        super.onDestroy()
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

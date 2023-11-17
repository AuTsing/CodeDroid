package com.autsing.codedroid.activities

import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.autsing.codedroid.R
import com.autsing.codedroid.ui.graphs.MainGraph
import com.autsing.codedroid.ui.theme.CodeDroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var tryFinishAt: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CodeDroidTheme {
                MainGraph()
            }
        }
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

package com.autsing.codedroid

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "CodeDroid"
        private const val DEFAULT_PROTOCOL = "http"
        private const val DEFAULT_IP = "127.0.0.1"
        private const val DEFAULT_PORT = "8080"
    }

    private lateinit var inputUrl: EditText
    private lateinit var textErrorMessage: TextView
    private lateinit var buttonGo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inputUrl = findViewById(R.id.input_url)
        textErrorMessage = findViewById(R.id.text_error_message)
        buttonGo = findViewById(R.id.button_go)

        @SuppressLint("SetTextI18n")
        inputUrl.setText("$DEFAULT_PROTOCOL://$DEFAULT_IP:$DEFAULT_PORT")
        buttonGo.performClick()
    }

    override fun onResume() {
        super.onResume()
        textErrorMessage.text = WebActivity.maybeException
        Log.d(TAG, "onResume: ${WebActivity.maybeException}")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun onClickGo(view: View) {
        val url = inputUrl.text.toString()
        WebActivity.startActivity(this, url)
    }
}

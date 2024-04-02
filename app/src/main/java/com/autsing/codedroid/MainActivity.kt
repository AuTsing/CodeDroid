package com.autsing.codedroid

import android.content.Intent
import android.os.Bundle
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
        const val DEFAULT_PROTOCOL = "http"
        const val DEFAULT_IP = "127.0.0.1"
        const val DEFAULT_PORT = "8080"
    }

    private lateinit var inputIp: EditText
    private lateinit var inputPort: EditText
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

        inputIp = findViewById(R.id.input_ip)
        inputPort = findViewById(R.id.input_port)
        textErrorMessage = findViewById(R.id.text_error_message)
        buttonGo = findViewById(R.id.button_go)

        inputIp.setText(DEFAULT_IP)
        inputPort.setText(DEFAULT_PORT)
        buttonGo.performClick()
    }

    override fun onResume() {
        super.onResume()
        WebActivity.maybeException?.let {
            textErrorMessage.text = it.message
        }
    }

    fun onClickGo(view: View) {
        val intent = Intent(this, WebActivity::class.java)
        intent.putExtra(WebActivity.EXTRA_KEY_IP, inputIp.text.toString())
        intent.putExtra(WebActivity.EXTRA_KEY_PORT, inputPort.text.toString())
        startActivity(intent)
    }
}

package com.autsing.codedroid

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val DEFAULT_URL = "http://127.0.0.1:8080"
private val KEY_URL = stringPreferencesKey("key_url")
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "main_prefs")

private suspend fun readUrl(context: Context): Result<String> = withContext(Dispatchers.IO) {
    runCatching {
        return@runCatching context.dataStore
            .data
            .map { it[KEY_URL] ?: DEFAULT_URL }
            .first()
    }
}

private suspend fun writeUrl(
    context: Context,
    url: String,
): Result<Unit> = withContext(Dispatchers.IO) {
    runCatching {
        context.dataStore
            .updateData {
                it.toMutablePreferences().also { preferences ->
                    preferences[KEY_URL] = url
                }
            }
        return@runCatching
    }
}

class MainActivity : AppCompatActivity() {

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
        inputUrl.addTextChangedListener {
            lifecycleScope.launch {
                runCatching {
                    writeUrl(this@MainActivity, it.toString()).getOrThrow()
                }
            }
        }
        textErrorMessage = findViewById(R.id.text_error_message)
        buttonGo = findViewById(R.id.button_go)

        lifecycleScope.launch {
            runCatching {
                val url = readUrl(this@MainActivity).getOrThrow()
                inputUrl.setText(url)
                buttonGo.performClick()
            }.onFailure {
                textErrorMessage.text = it.message
            }
        }
    }

    override fun onResume() {
        super.onResume()
        textErrorMessage.text = WebActivity.maybeException
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

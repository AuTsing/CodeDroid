package com.autsing.codedroid.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.autsing.codedroid.R
import com.autsing.codedroid.ui.graphs.MainGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import java.lang.ref.WeakReference

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        var instance: WeakReference<ComponentActivity> = WeakReference(null)

        suspend fun requestFileChosen(
            intent: Intent,
        ): Result<Channel<Result<Array<Uri>>>> = runCatching {
            withTimeout(3000) {
                while (instance.get() == null) delay(10)
            }
            val activity = instance.get()!! as MainActivity
            val channel = Channel<Result<Array<Uri>>>()
            lateinit var launcher: ActivityResultLauncher<Intent>
            launcher = activity.activityResultRegistry.register(
                "requestFileChosen",
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
                        channel.trySend(Result.success(uris.toTypedArray()))
                    }

                    RESULT_CANCELED -> channel.trySend(Result.success(emptyArray()))
                    RESULT_FIRST_USER -> channel.trySend(Result.failure(Exception("RESULT_FIRST_USER")))
                }
                launcher.unregister()
            }
            launcher.launch(intent)
            return@runCatching channel
        }
    }

    private var tryFinishAt: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = WeakReference(this)
        setContent { MainGraph() }
    }

    override fun onDestroy() {
        instance = WeakReference(null)
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

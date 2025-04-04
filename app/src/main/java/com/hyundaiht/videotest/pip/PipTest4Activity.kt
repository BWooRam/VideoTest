package com.hyundaiht.videotest.pip

import android.app.ComponentCaller
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.hyundaiht.videotest.ui.theme.VideoTestTheme


class PipTest4Activity : ComponentActivity() {
    private val tag = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoTestTheme {
                PiPButtonScreen(onEnterPiP = {
                    enterPiPMode()
                })
            }
        }
        Log.d(tag, "onCreate action = ${intent.action}")
        handleIntentAction(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(tag, "onNewIntent action = ${intent.action}")
        handleIntentAction(intent)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(tag, "onConfigurationChanged newConfig = $newConfig}")
    }

    private fun handleIntentAction(intent: Intent) {
        when (intent.action) {
            "ACTION_PLAY" -> showToast("재생 버튼 클릭됨!")
            "ACTION_PAUSE" -> showToast("일시 정지 버튼 클릭됨!")
            "ACTION_STOP" -> showToast("중지 버튼 클릭됨!")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@PipTest4Activity, message, Toast.LENGTH_SHORT).show()
    }

    @Composable
    fun PiPButtonScreen(onEnterPiP: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), contentAlignment = Alignment.Center
        ) {
            Button(onClick = onEnterPiP) {
                Text("PiP 모드로 전환")
            }
        }
    }

    private fun enterPiPMode() {
        val playIntent = Intent(this, PipTest4Activity::class.java).apply {
            action = "ACTION_PLAY"
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pauseIntent = Intent(this, PipTest4Activity::class.java).apply {
            action = "ACTION_PAUSE"
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val stopIntent = Intent(this, PipTest4Activity::class.java).apply {
            action = "ACTION_STOP"
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val timestamp = System.currentTimeMillis().toInt()
        val playPendingIntent = PendingIntent.getActivity(
            this, timestamp, playIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pausePendingIntent = PendingIntent.getActivity(
            this, timestamp, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopPendingIntent = PendingIntent.getActivity(
            this, timestamp, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playAction = RemoteAction(
            Icon.createWithResource(this, android.R.drawable.ic_media_play),
            "Play",
            "Play Button",
            playPendingIntent
        )

        val pauseAction = RemoteAction(
            Icon.createWithResource(this, android.R.drawable.ic_media_pause),
            "Pause",
            "Pause Button",
            pausePendingIntent
        )

        val stopAction = RemoteAction(
            Icon.createWithResource(this, android.R.drawable.ic_media_next),
            "Stop",
            "Stop Button",
            stopPendingIntent
        )

        val pipParams = PictureInPictureParams.Builder()
            .setAspectRatio(Rational(1, 1))
            .setActions(listOf(playAction, pauseAction, stopAction))
            .build()

        enterPictureInPictureMode(pipParams)
        showToast("PiP 모드로 전환됨")
    }
}
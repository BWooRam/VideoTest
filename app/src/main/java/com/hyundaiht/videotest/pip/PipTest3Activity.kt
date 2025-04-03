package com.hyundaiht.videotest.pip

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Intent
import android.content.IntentFilter
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


class PipTest3Activity : ComponentActivity() {
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
        pipEvent.observe(this@PipTest3Activity) { value ->
            when(value){
                "ACTION_PLAY" -> {
                    Log.d(tag, "PiPActionReceiver onPlay")
                    showToast("재생 버튼 클릭됨!")
                }
                "ACTION_PAUSE" -> {
                    Log.d(tag, "PiPActionReceiver onPause")
                    showToast("일시 정지 버튼 클릭됨!")
                }
                "ACTION_STOP" -> {
                    Log.d(tag, "PiPActionReceiver onStop")
                    showToast("중지 버튼 클릭됨!")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(tag, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(tag, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop")
    }

    private fun showToast(message: String) {
        Toast.makeText(this@PipTest3Activity, message, Toast.LENGTH_SHORT).show()
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
        // 각 버튼에 대한 인텐트와 액션 설정
        val playIntent = Intent(this, PiPActionReceiver::class.java).apply {
            action = "ACTION_PLAY"
        }
        val pauseIntent = Intent(this, PiPActionReceiver::class.java).apply {
            action = "ACTION_PAUSE"
        }
        val stopIntent = Intent(this, PiPActionReceiver::class.java).apply {
            action = "ACTION_STOP"
        }

        val timestamp = System.currentTimeMillis().toInt()
        val playPendingIntent = PendingIntent.getBroadcast(
            this,
            timestamp,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val pausePendingIntent = PendingIntent.getBroadcast(
            this,
            timestamp,
            pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            timestamp,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
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

        val pipParams = PictureInPictureParams.Builder().setAspectRatio(Rational(1, 1))
            .setActions(listOf(playAction, pauseAction, stopAction)) // 버튼 3개 추가
            .build()

        enterPictureInPictureMode(pipParams)
        Toast.makeText(this, "PiP 모드로 전환됨", Toast.LENGTH_SHORT).show()
    }

    companion object {
        val pipEvent = MutableLiveData<String>()
    }
}
package com.hyundaiht.videotest.pip

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.hyundaiht.videotest.R
import com.hyundaiht.videotest.ui.theme.VideoTestTheme


class PipTest3Activity : ComponentActivity() {
    private val tag = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoTestTheme {
                PiPButtonScreen(
                    onEnterPiP = {
                        enterPiPMode()
                    },
                    onPower = {
                        pipEvent.postValue(Control.ACTION_POWER)
                    },
                    onUp = {
                        pipEvent.postValue(Control.ACTION_UP)
                    },
                    onDown = {
                        pipEvent.postValue(Control.ACTION_DOWN)
                    },
                )
            }
        }
        pipEvent.observe(this@PipTest3Activity) { value ->
            when (value) {
                Control.ACTION_POWER -> {
                    Log.d(tag, "PiPActionReceiver ACTION_POWER")
                    showToast("전원 켜짐!")
                }

                Control.ACTION_UP -> {
                    Log.d(tag, "PiPActionReceiver ACTION_UP")
                    showToast("온도 상승!")
                }

                Control.ACTION_DOWN -> {
                    Log.d(tag, "PiPActionReceiver ACTION_DOWN")
                    showToast("온도 하강!")
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
    fun PiPButtonScreen(
        onEnterPiP: () -> Unit,
        onPower: () -> Unit,
        onUp: () -> Unit,
        onDown: () -> Unit,
    ) {
        val rememberScroll = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScroll),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.device_ic_detail_aircon_active),
                contentDescription = "My Drawable Image",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .background(Color.Yellow),
                contentScale = ContentScale.Fit
            )
            Button(onClick = onEnterPiP) {
                Text("PiP 모드로 전환")
            }
            Button(onClick = onPower) {
                Text("전원")
            }
            Button(onClick = onUp) {
                Text("Up")
            }

            Button(onClick = onDown) {
                Text("Down")
            }
        }
    }

    private fun enterPiPMode() {
        // 각 버튼에 대한 인텐트와 액션 설정
        val playIntent = Intent(this, PiPActionReceiver::class.java).apply {
            action = Control.ACTION_POWER
        }
        val pauseIntent = Intent(this, PiPActionReceiver::class.java).apply {
            action = Control.ACTION_UP
        }
        val stopIntent = Intent(this, PiPActionReceiver::class.java).apply {
            action = Control.ACTION_DOWN
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
            Icon.createWithResource(this, android.R.drawable.ic_lock_power_off),
            "Power",
            "Power Button",
            playPendingIntent
        )

        val pauseAction = RemoteAction(
            Icon.createWithResource(this, android.R.drawable.arrow_up_float),
            "Up",
            "Up Button",
            pausePendingIntent
        )

        val stopAction = RemoteAction(
            Icon.createWithResource(this, android.R.drawable.arrow_down_float),
            "Down",
            "Down Button",
            stopPendingIntent
        )

        val pipParamsBuilder = PictureInPictureParams.Builder()
            .setAspectRatio(Rational(1, 1))
            .setSourceRectHint(android.graphics.Rect(100, 100, 500, 500))
            .setActions(listOf(playAction, pauseAction, stopAction)) // 버튼 3개 추가
        //            .setExpandedAspectRatio()
        //            .setSeamlessResizeEnabled()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pipParamsBuilder.setSubtitle("Subtitle")
            pipParamsBuilder.setTitle("Title")
        }

        val pipParams = pipParamsBuilder.build()


        enterPictureInPictureMode(pipParams)
        Toast.makeText(this, "PiP 모드로 전환됨", Toast.LENGTH_SHORT).show()
    }

    companion object {
        val pipEvent = MutableLiveData<String>()
    }
}

/**
 *
 */
object Control {
    const val ACTION_POWER = "ACTION_POWER"
    const val ACTION_UP = "ACTION_UP"
    const val ACTION_DOWN = "ACTION_DOWN"
}
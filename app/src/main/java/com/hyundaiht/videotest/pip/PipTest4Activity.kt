package com.hyundaiht.videotest.pip

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hyundaiht.videotest.R
import com.hyundaiht.videotest.ui.theme.VideoTestTheme


class PipTest4Activity : ComponentActivity() {
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
                        executeEvent(Control.ACTION_POWER)
                    },
                    onUp = {
                        executeEvent(Control.ACTION_UP)
                    },
                    onDown = {
                        executeEvent(Control.ACTION_DOWN)
                    },
                )
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
        val action = intent.action ?: return
        executeEvent(action)
    }

    private fun executeEvent(action: String) {
        when (action) {
            Control.ACTION_POWER -> showToast("전원 켜짐!")
            Control.ACTION_UP -> showToast("온도 상승!")
            Control.ACTION_DOWN -> showToast("온도 하강!")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@PipTest4Activity, message, Toast.LENGTH_SHORT).show()
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
        val playIntent = Intent(this, PipTest4Activity::class.java).apply {
            action = Control.ACTION_POWER
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pauseIntent = Intent(this, PipTest4Activity::class.java).apply {
            action = Control.ACTION_UP
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val stopIntent = Intent(this, PipTest4Activity::class.java).apply {
            action = Control.ACTION_DOWN
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val timestamp = System.currentTimeMillis().toInt()
        val playPendingIntent = PendingIntent.getActivity(
            this,
            timestamp,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pausePendingIntent = PendingIntent.getActivity(
            this,
            timestamp,
            pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopPendingIntent = PendingIntent.getActivity(
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

        val pipParams = PictureInPictureParams.Builder()
            .setAspectRatio(Rational(1, 1))
            .setSourceRectHint(android.graphics.Rect(100, 100, 500, 500))
            .setActions(listOf(playAction, pauseAction, stopAction))
            .build()

        enterPictureInPictureMode(pipParams)
        showToast("PiP 모드로 전환됨")
    }
}
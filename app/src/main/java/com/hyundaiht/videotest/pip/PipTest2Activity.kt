package com.hyundaiht.videotest.pip

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.hyundaiht.videotest.ui.theme.VideoTestTheme


class PipTest2Activity : ComponentActivity() {

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoTestTheme {
                val inPipMode = rememberIsInPipMode()

                Column {
                    Text("PipTest2Activity inPipMode = $inPipMode")
                    TEST()
                }
            }
        }
    }

    @Composable
    fun TEST(
        modifier: Modifier = Modifier
    ) {
        val context = LocalContext.current
        val params = PictureInPictureParams.Builder()
            .setAspectRatio(Rational(16, 9))

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            DisposableEffect(context) {
                val onUserLeaveBehavior: () -> Unit = {
                    context.findActivity()
                        .enterPictureInPictureMode(params.build())
                }
                context.findActivity().addOnUserLeaveHintListener(
                    onUserLeaveBehavior
                )
                onDispose {
                    context.findActivity().removeOnUserLeaveHintListener(
                        onUserLeaveBehavior
                    )
                }
            }
        } else {
            modifier.then(Modifier.onGloballyPositioned { layoutCoordinates ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    params.setAutoEnterEnabled(true)
                }
                context.findActivity().setPictureInPictureParams(params.build())
            })
        }

        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(this)) // 👈 여기가 핵심
            .build().apply {
                val mediaItem = MediaItem.Builder()
                    .setUri("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8")
                    .build()
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }

        AndroidView(modifier = modifier.then(Modifier.fillMaxSize()), factory = { context ->
            PlayerView(context).apply {
                this.player = player
                useController = false // 필요 시 true
            }
        })
    }
}
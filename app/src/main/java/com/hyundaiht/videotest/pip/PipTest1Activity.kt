package com.hyundaiht.videotest.pip

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView


class PipTest1Activity : ComponentActivity() {
    private val tag = javaClass.simpleName
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        setContent {
            AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
                PlayerView(context).apply {
                    this.player = player
                    useController = false // 필요 시 true
                }
            })
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        enterPipMode()
    }

    private fun enterPipMode() {
        val params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PictureInPictureParams.Builder()
//                .setAspectRatio(Rational(16, 9))
//                .setAutoEnterEnabled(true)
        } else {
            PictureInPictureParams.Builder()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d(tag, "enterPipMode add Title, Subtitle")
            params.setTitle("Title")
                .setSubtitle("Subtitle")
                .setAspectRatio(Rational(16, 9))
                .setExpandedAspectRatio(Rational(21, 9))
                .setSeamlessResizeEnabled(false)
        }

        enterPictureInPictureMode(params.build())
    }

    override fun onStop() {
        super.onStop()
        if (!isInPictureInPictureMode) {
            player?.release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean, newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }
}
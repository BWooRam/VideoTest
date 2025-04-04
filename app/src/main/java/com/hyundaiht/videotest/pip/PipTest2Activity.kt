package com.hyundaiht.videotest.pip

import android.app.PictureInPictureParams
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toRect
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.hyundaiht.videotest.ui.theme.VideoTestTheme


class PipTest2Activity : ComponentActivity() {
    private val tag = javaClass.simpleName
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoTestTheme {
                val inPipMode = rememberIsInPipMode()

                Column {
//                    Text("PipTest2Activity inPipMode = $inPipMode")
                    TEST()
                }
            }
        }
    }

    @Composable
    fun TEST() {
        val context = LocalContext.current
        val params = PictureInPictureParams.Builder()
            .setAspectRatio(Rational(16, 9))
        //상태를 사용하여 PIP 모드로 전환되는지 정의
        val currentShouldEnterPipMode by rememberUpdatedState(newValue = shouldEnterPipMode)
        var currentPlayState by remember { mutableIntStateOf(0) }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            DisposableEffect(context) {
                Log.d(tag, "init DisposableEffect")
                val onUserLeaveBehavior: () -> Unit = {
                    //사용자가 홈 버튼을 누르거나 다른 앱으로 전환하는 등으로 인해 현재 액티비티가 백그라운드로 전환되기 직전에 호출
                    Log.d(tag, "onUserLeaveBehavior event start")
                    if (currentShouldEnterPipMode) {
                        context.findActivity()
                            .enterPictureInPictureMode(params.build())
                    }
                }
                context.findActivity().addOnUserLeaveHintListener(
                    onUserLeaveBehavior
                )
                onDispose {
                    Log.d(tag, "onUserLeaveBehavior onDispose event start")
                    context.findActivity().removeOnUserLeaveHintListener(
                        onUserLeaveBehavior
                    )
                }
            }
        }

        val player = ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(context)) // 👈 여기가 핵심
            .build().apply {
                val mediaItem = MediaItem.Builder()
                    .setUri("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8")
                    .build()
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
                /*addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        Log.d(tag, "onIsPlayingChanged isPlaying = $isPlaying")
                        shouldEnterPipMode = isPlaying
                    }
                })*/
            }
        Column {
            AndroidView(modifier = Modifier
                .wrapContentSize()
                .onGloballyPositioned { layoutCoordinates ->
                    Log.d(
                        tag,
                        "onGloballyPositioned event start currentShouldEnterPipMode = $currentShouldEnterPipMode"
                    )

                    if (currentShouldEnterPipMode) {
                        val sourceRect = layoutCoordinates.boundsInWindow().toAndroidRectF().toRect()
                        params.setSourceRectHint(sourceRect)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        params.setAutoEnterEnabled(currentShouldEnterPipMode)
                    }

                    context
                        .findActivity()
                        .setPictureInPictureParams(params.build())
                }, factory = { context ->
                PlayerView(context).apply {
                    this.player = player
                    useController = false // 필요 시 true
                }
            }, update = { view ->
                when (currentPlayState) {
                    1 -> {
                        view.player?.play()
                        shouldEnterPipMode = true
                    }

                    2 -> {
                        view.player?.pause()
                        shouldEnterPipMode = false
                    }

                    3 -> {
                        view.player?.stop()
                        shouldEnterPipMode = false
                    }
                }
            })
            Row {
                Button(onClick = {
                    currentPlayState = 1
                }) {
                    Text("재생")
                }

                Button(onClick = {
                    currentPlayState = 2
                }) {
                    Text("일시 정지")
                }

                Button(onClick = {
                    currentPlayState = 3
                }) {
                    Text("정지")
                }
            }
        }
    }
}

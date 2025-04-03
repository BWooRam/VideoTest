package com.hyundaiht.videotest.pip

import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Density
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.PictureInPictureModeChangedInfo
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRect
import androidx.core.util.Consumer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView

var shouldEnterPipMode by mutableStateOf(false)
private const val PIP_TAG = "PiP info"

// [START android_compose_pip_broadcast_receiver_constants]
// Constant for broadcast receiver
const val ACTION_BROADCAST_CONTROL = "broadcast_control"

// Intent extras for broadcast controls from Picture-in-Picture mode.
const val EXTRA_CONTROL_TYPE = "control_type"
const val EXTRA_CONTROL_PLAY = 1
const val EXTRA_CONTROL_PAUSE = 2

@Composable
fun PiPBuilderSetAutoEnterEnabled(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val pipModifier = modifier.onGloballyPositioned { layoutCoordinates ->
            val builder = PictureInPictureParams.Builder()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                builder.setAutoEnterEnabled(true)
            }
            context.findActivity().setPictureInPictureParams(builder.build())
        }
        VideoPlayer(pipModifier)
    } else {
        Log.i(PIP_TAG, "API does not support PiP")
    }
}

fun Modifier.pipAutoEnter(enabled: Boolean, builder: PictureInPictureParams.Builder): Modifier {
    return this.then(
        Modifier.onGloballyPositioned {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                builder.setAutoEnterEnabled(enabled)
            }
        }
    )
}

fun Modifier.pipSourceRectHint(builder: PictureInPictureParams.Builder): Modifier {
    return this.then(
        Modifier.onGloballyPositioned { coordinates ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val bounds = coordinates.boundsInWindow()
                val rect = Rect(
                    bounds.left.toInt(),
                    bounds.top.toInt(),
                    bounds.right.toInt(),
                    bounds.bottom.toInt()
                )
                builder.setSourceRectHint(rect)
            }
        }
    )
}

internal fun Context.findActivity(): ComponentActivity {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Picture in picture should be called in the context of an Activity")
}

@Composable
fun EnterPiPThroughButton() {
    val context = LocalContext.current
    Button(onClick = {
        context.findActivity().enterPictureInPictureMode(
            PictureInPictureParams.Builder().build()
        )
    }) {
        Text(text = "Enter PiP mode!")
    }
}

@Composable
fun rememberIsInPipMode(): Boolean {
    val activity = LocalContext.current.findActivity()
    var pipMode by remember { mutableStateOf(activity.isInPictureInPictureMode) }
    DisposableEffect(activity) {
        val observer = Consumer<PictureInPictureModeChangedInfo> { info ->
            pipMode = info.isInPictureInPictureMode
        }
        activity.addOnPictureInPictureModeChangedListener(
            observer
        )
        onDispose { activity.removeOnPictureInPictureModeChangedListener(observer) }
    }
    return pipMode
}

@Composable
fun VideoPlayer() {

}
@Composable
fun VideoPlayer(modifier: Modifier) {

}

@Composable
fun ToggleUIBasedOnPiP(
    modifier: Modifier = Modifier,
) {
    // [START android_compose_pip_ui_toggle]
    val inPipMode = rememberIsInPipMode()

    Column(modifier = modifier) {
        if (!inPipMode) {
            Text(
                text = "Picture in Picture",
            )
        }
        VideoPlayer()
    }
}

fun initializePlayer(context: Context) {
    val player = ExoPlayer.Builder(context.applicationContext)
        .build().apply {}

    player.addListener(object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            shouldEnterPipMode = isPlaying
        }
    })
}

fun releasePlayer() {
    shouldEnterPipMode = false
}

@Composable
fun PiPBuilderSetAutoEnterEnabledUsingState(
    shouldEnterPipMode: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val pipModifier = modifier.onGloballyPositioned { layoutCoordinates ->
            val builder = PictureInPictureParams.Builder()

            // Add autoEnterEnabled for versions S and up
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                builder.setAutoEnterEnabled(shouldEnterPipMode)
            }
            context.findActivity().setPictureInPictureParams(builder.build())
        }

        VideoPlayer(pipModifier)
    } else {
        Log.i(PIP_TAG, "API does not support PiP")
    }
}

@Composable
fun PiPBuilderSetSourceRect(
    shouldEnterPipMode: Boolean,
    modifier: Modifier = Modifier,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val context = LocalContext.current

        val pipModifier = modifier.onGloballyPositioned { layoutCoordinates ->
            val builder = PictureInPictureParams.Builder()
            if (shouldEnterPipMode) {
                val sourceRect = layoutCoordinates.boundsInWindow().toAndroidRectF().toRect()
                builder.setSourceRectHint(sourceRect)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                builder.setAutoEnterEnabled(shouldEnterPipMode)
            }
            context.findActivity().setPictureInPictureParams(builder.build())
        }

        VideoPlayer(pipModifier)
    } else {
        Log.i(PIP_TAG, "API does not support PiP")
    }
}

@Composable
fun PiPBuilderSetAspectRatio(
    player: Player?,
    shouldEnterPipMode: Boolean,
    modifier: Modifier = Modifier,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // [START android_compose_pip_set_aspect_ratio]
        val context = LocalContext.current

        val pipModifier = modifier.onGloballyPositioned { layoutCoordinates ->
            val builder = PictureInPictureParams.Builder()
            if (shouldEnterPipMode && player != null && player.videoSize != VideoSize.UNKNOWN) {
                val sourceRect = layoutCoordinates.boundsInWindow().toAndroidRectF().toRect()
                builder.setSourceRectHint(sourceRect)
                builder.setAspectRatio(
                    Rational(player.videoSize.width, player.videoSize.height)
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                builder.setAutoEnterEnabled(shouldEnterPipMode)
            }
            context.findActivity().setPictureInPictureParams(builder.build())
        }

        VideoPlayer(pipModifier)
        // [END android_compose_pip_set_aspect_ratio]
    } else {
        Log.i(PIP_TAG, "API does not support PiP")
    }
}

// [START android_compose_pip_broadcast_receiver]
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlayerBroadcastReceiver(player: Player?) {
    val isInPipMode = rememberIsInPipMode()
    if (!isInPipMode || player == null) {
        // Broadcast receiver is only used if app is in PiP mode and player is non null
        return
    }
    val context = LocalContext.current

    DisposableEffect(player) {
        val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if ((intent == null) || (intent.action != ACTION_BROADCAST_CONTROL)) {
                    return
                }

                when (intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)) {
                    EXTRA_CONTROL_PAUSE -> player.pause()
                    EXTRA_CONTROL_PLAY -> player.play()
                }
            }
        }
        ContextCompat.registerReceiver(
            context,
            broadcastReceiver,
            IntentFilter(ACTION_BROADCAST_CONTROL),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        onDispose {
            context.unregisterReceiver(broadcastReceiver)
        }
    }
}
// [END android_compose_pip_broadcast_receiver]

@RequiresApi(Build.VERSION_CODES.O)
fun listOfRemoteActions(): List<RemoteAction> {
    return listOf()
}

@Composable
fun PiPBuilderAddRemoteActions(
    player: Player?,
    shouldEnterPipMode: Boolean,
    modifier: Modifier = Modifier,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // [START android_compose_pip_add_remote_actions]
        val context = LocalContext.current

        val pipModifier = modifier.onGloballyPositioned { layoutCoordinates ->
            val builder = PictureInPictureParams.Builder()
            builder.setActions(
                listOfRemoteActions()
            )

            if (shouldEnterPipMode && player != null && player.videoSize != VideoSize.UNKNOWN) {
                val sourceRect = layoutCoordinates.boundsInWindow().toAndroidRectF().toRect()
                builder.setSourceRectHint(sourceRect)
                builder.setAspectRatio(
                    Rational(player.videoSize.width, player.videoSize.height)
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                builder.setAutoEnterEnabled(shouldEnterPipMode)
            }
            context.findActivity().setPictureInPictureParams(builder.build())
        }
        VideoPlayer(modifier = pipModifier)
        // [END android_compose_pip_add_remote_actions]
    } else {
        Log.i(PIP_TAG, "API does not support PiP")
    }
}

@Composable
fun PipListenerPreAPI12(shouldEnterPipMode: Boolean) {
    // [START android_compose_pip_pre12_listener]
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S
    ) {
        val context = LocalContext.current
        DisposableEffect(context) {
            val onUserLeaveBehavior: () -> Unit = {
                context.findActivity()
                    .enterPictureInPictureMode(PictureInPictureParams.Builder().build())
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
        Log.i("PiP info", "API does not support PiP")
    }
    // [END android_compose_pip_pre12_listener]
}

@Composable
fun EnterPiPPre12(shouldEnterPipMode: Boolean) {
    // [START android_compose_pip_pre12_should_enter_pip]
    val currentShouldEnterPipMode by rememberUpdatedState(newValue = shouldEnterPipMode)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S
    ) {
        val context = LocalContext.current
        DisposableEffect(context) {
            val onUserLeaveBehavior: () -> Unit = {
                if (currentShouldEnterPipMode) {
                    context.findActivity()
                        .enterPictureInPictureMode(PictureInPictureParams.Builder().build())
                }
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
        Log.i("PiP info", "API does not support PiP")
    }
    // [END android_compose_pip_pre12_should_enter_pip]
}

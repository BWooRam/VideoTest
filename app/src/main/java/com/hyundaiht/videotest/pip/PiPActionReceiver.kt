package com.hyundaiht.videotest.pip

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class PiPActionReceiver: BroadcastReceiver() {
    private val tag = javaClass.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(tag, "onReceive action = ${intent.action}")
        when (intent.action) {
            "ACTION_PLAY" -> PipTest3Activity.pipEvent.postValue("ACTION_PLAY")
            "ACTION_PAUSE" -> PipTest3Activity.pipEvent.postValue("ACTION_PAUSE")
            "ACTION_STOP" -> PipTest3Activity.pipEvent.postValue("ACTION_STOP")
        }
    }
}

interface PipAction {
    fun onPlay()
    fun onPause()
    fun onStop()
}
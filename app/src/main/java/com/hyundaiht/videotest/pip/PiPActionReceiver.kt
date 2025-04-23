package com.hyundaiht.videotest.pip

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class PiPActionReceiver: BroadcastReceiver() {
    private val tag = javaClass.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(tag, "onReceive action = ${intent.action}")
        PipTest3Activity.pipEvent.postValue(intent.action)
    }
}
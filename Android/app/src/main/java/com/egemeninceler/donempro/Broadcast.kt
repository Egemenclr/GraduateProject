package com.egemeninceler.donempro

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class Broadcast: BroadcastReceiver() {

    override fun onReceive(p0: Context?, intent: Intent?) {
        val message = intent!!.getStringExtra("message")
        Log.d("receiver", "Got message: $message")

    }

}
package com.example.notificationtimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class TimerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, TimerService::class.java).apply {
            action = intent.action
        }
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}

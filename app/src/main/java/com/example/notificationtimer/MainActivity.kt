package com.example.notificationtimer

import android.content.Intent
import android.os.Bundle
import android.app.Activity

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, TimerService::class.java).apply {
            action = "START"
        }
        startForegroundService(intent)
        finish()
    }
}

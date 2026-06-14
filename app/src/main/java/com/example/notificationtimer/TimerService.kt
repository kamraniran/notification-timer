package com.example.notificationtimer

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat

class TimerService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var seconds = 0
    private var isRunning = false
    private val CHANNEL_ID = "timer_channel"
    private val NOTIF_ID = 1

    private val runnable = object : Runnable {
        override fun run() {
            seconds++
            updateNotification()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(NOTIF_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START" -> {
                if (!isRunning) {
                    isRunning = true
                    handler.post(runnable)
                }
            }
            "STOP" -> {
                isRunning = false
                handler.removeCallbacks(runnable)
                updateNotification()
            }
            "RESET" -> {
                isRunning = false
                handler.removeCallbacks(runnable)
                seconds = 0
                updateNotification()
            }
        }
        return START_STICKY
    }

    private fun buildNotification(): Notification {
        val minutes = seconds / 60
        val secs = seconds % 60
        val timeText = String.format("%02d:%02d", minutes, secs)

        val startIntent = PendingIntent.getBroadcast(
            this, 0,
            Intent(this, TimerReceiver::class.java).apply { action = "START" },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = PendingIntent.getBroadcast(
            this, 1,
            Intent(this, TimerReceiver::class.java).apply { action = "STOP" },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val resetIntent = PendingIntent.getBroadcast(
            this, 2,
            Intent(this, TimerReceiver::class.java).apply { action = "RESET" },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Timer")
            .setContentText(timeText)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .addAction(0, "Start", startIntent)
            .addAction(0, "Stop", stopIntent)
            .addAction(0, "Reset", resetIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIF_ID, buildNotification())
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID, "Timer Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

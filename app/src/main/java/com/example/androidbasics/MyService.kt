package com.example.androidbasics

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MyService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.Start.toString() -> {
                start()
            }

            Actions.Stop.toString() -> {
                stopSelf()
                println("service is stopped!")
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "Channel_ID")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("MyService")
            .setContentText("App is running")
            .build()
        startForeground(0x0118, notification)
        println("service is started!")
    }

    enum class Actions {
        Start,
        Stop
    }
}
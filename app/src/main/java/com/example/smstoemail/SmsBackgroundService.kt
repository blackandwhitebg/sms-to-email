package com.example.smstoemail

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class SmsBackgroundService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TAG", "Service started.")

        val channelID = "my_channel_01"
        val channel = NotificationChannel(channelID, "B&W SMS to E-mail Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            channel
        )

        val notification = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("B&W SMS to E-mail Service is running")
            .setContentText("The service is forwarding SMS to E-mail").build()

        startForeground(1, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        startService(Intent(this, SmsBackgroundService::class.java))
        Log.v("App", "Service Started Again")
    }

    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }
}
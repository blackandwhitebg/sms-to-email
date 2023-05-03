package com.example.smstoemail

import android.R.attr.data
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action) {
            Log.d("TAG", "MyReceiver")
            val serviceIntent = Intent(context, SmsBackgroundService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}
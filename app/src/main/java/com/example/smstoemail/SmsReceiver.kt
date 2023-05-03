package com.example.smstoemail

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class SmsReceiver : BroadcastReceiver() {

    companion object {
        private val TAG by lazy { SmsReceiver::class.java.simpleName }
    }

    private var dataUtils = DataUtils()

    override fun onReceive(context: Context, intent: Intent) {
        Log.v("App", "SMS Received!")
        val message = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val content = message[0].displayMessageBody
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()

        val localDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val output = formatter.format(localDateTime)

        dataUtils.saveLog(context, "$output: $content")

        Thread(Runnable {
            SmtpManager().sendEmail(context, "$output: $content")
        }).start()
    }
}
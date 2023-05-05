package com.example.smstoemail

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SmsReceiver : BroadcastReceiver() {

    private var dataUtils = DataUtils()

    override fun onReceive(context: Context, intent: Intent) {
        Log.v("App", "SMS Received!")
        val message = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val content = message[0].displayMessageBody
        //Toast.makeText(context, content, Toast.LENGTH_SHORT).show()

        if (filterMsg(content, context)) {
            return
        }

        val localDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val output = formatter.format(localDateTime)

        dataUtils.saveLog(context, "$output: $content")

        Thread {
            SmtpManager().sendEmail(context, "$output: $content")
        }.start()
    }

    private fun filterMsg(msg: String, context: Context) : Boolean {
        val filters = dataUtils.loadFilterContains(context)

        if (filters.isBlank()) {
            return false
        }

        var allowed = false

        for (line in filters.lines()) {
            if (msg.contains(line, ignoreCase = true)) {
                allowed = true
                break
            }
        }

        return !allowed
    }
}
package com.bnwsoft.smstoemail

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
        val origin = message[0].displayOriginatingAddress
        //Toast.makeText(context, content, Toast.LENGTH_SHORT).show()

        if (filterMsg(content, context)) {
            return
        }

        val localDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val output = formatter.format(localDateTime)

        val textToSave = "$output: $content ($origin)"

        dataUtils.saveLog(context, textToSave)

        val fwdSel = dataUtils.loadSmtpData(context, dataUtils.fwdSelection)
        if (fwdSel == "E-mail") {
            Thread {
                SmtpManager().sendEmail(context, textToSave, origin)
            }.start()
        }
        else if (fwdSel == "SMS") {
            SmsSender().sendSms(context, content)
        }
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
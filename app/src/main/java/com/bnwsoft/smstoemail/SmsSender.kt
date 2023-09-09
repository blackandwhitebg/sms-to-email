package com.bnwsoft.smstoemail

import android.content.Context
import android.os.Build
import android.telephony.SmsManager

class SmsSender {
    fun sendSms(context: Context, smsText: String) {
        val dataUtils = DataUtils()
        val toPhone = dataUtils.loadSmtpData(context, dataUtils.smsToPhone)

        val  smsManager : SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService<SmsManager>(SmsManager::class.java)
        } else {
            SmsManager.getDefault()
        }

        smsManager.sendTextMessage(toPhone, null, smsText, null, null)
    }
}
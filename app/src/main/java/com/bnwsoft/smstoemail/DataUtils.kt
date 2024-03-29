package com.bnwsoft.smstoemail

import android.content.Context
import android.util.Log

class DataUtils {
    private val appPreferences = "BW_SMSTOEMAIL_PREF"

    private val logText = "LOG_TEXT"
    private val filterContains = "FILTER_CONTAINS"

    val fwdSelection = "FWD_SELECTION"

    val smtpHost = "SMTP_HOST"
    val smtpPort = "SMTP_PORT"
    val smtpEmail = "SMTP_EMAIL"
    val smtpPass = "SMTP_PASS"
    val smtpToEmail = "SMTP_TO_EMAIL"

    val smsToPhone = "SMS_TO_PHONE"

    fun loadLog(context: Context) : String {
        val sharedPreferences = context.getSharedPreferences(appPreferences, Context.MODE_PRIVATE)
        val current = sharedPreferences.getString(logText, "")
        return current.toString()
    }

    fun saveLog(context: Context, content: String) {
        val sharedPreferences = context.getSharedPreferences(appPreferences, Context.MODE_PRIVATE)
        val current = sharedPreferences.getString(logText, "")
        val txtToSave = current + "\n\n" + content
        sharedPreferences.edit().putString(logText, txtToSave.trim()).apply()
    }

    fun loadFilterContains(context: Context) : String {
        val sharedPreferences = context.getSharedPreferences(appPreferences, Context.MODE_PRIVATE)
        val current = sharedPreferences.getString(filterContains, "")
        return current.toString()
    }

    fun saveFilterContains(context: Context, content: String) {
        val sharedPreferences = context.getSharedPreferences(appPreferences, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(filterContains, content).apply()
    }

    fun saveSmtpData(context: Context, key: String, value: String) {
        val sharedPreferences = context.getSharedPreferences(appPreferences, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(key, value).apply()
        Log.i("Data", "Save $key -> $value")
    }

    fun loadSmtpData(context: Context, key: String) : String {
        val sharedPreferences = context.getSharedPreferences(appPreferences, Context.MODE_PRIVATE)
        val current = sharedPreferences.getString(key, "")
        Log.i("Data", "Load $key -> $current")
        return current.toString()
    }
}
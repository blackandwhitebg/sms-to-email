package com.example.smstoemail

import android.content.Context
import android.util.Log

class DataUtils {
    val MY_PREF = "BW_SMSTOEMAIL_PREF"

    val LOG_TEXT = "LOG_TEXT"

    val SMTP_HOST = "SMTP_HOST"
    val SMTP_PORT = "SMTP_PORT"
    val SMTP_EMAIL = "SMTP_EMAIL"
    val SMTP_PASS = "SMTP_PASS"

    public fun loadLog(context: Context) : String {
        val sharedPreferences = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE)
        var current = sharedPreferences.getString(LOG_TEXT, "")
        return current.toString();
    }

    public fun saveLog(context: Context, content: String) {
        val sharedPreferences = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE)
        var current = sharedPreferences.getString(LOG_TEXT, "")
        sharedPreferences.edit().putString(LOG_TEXT, current + "\n" + content).commit()
    }

    public fun saveSmtpData(context: Context, key: String, value: String) {
        val sharedPreferences = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(key, value).commit()
        Log.i("Data", "Save $key -> $value")
    }

    public fun loadSmtpData(context: Context, key: String) : String {
        val sharedPreferences = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE)
        var current = sharedPreferences.getString(key, "")
        Log.i("Data", "Load $key -> $current")
        return current.toString()
    }
}
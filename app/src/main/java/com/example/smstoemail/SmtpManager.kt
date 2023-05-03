package com.example.smstoemail

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import java.util.*
import javax.mail.*
import javax.mail.internet.*

class SmtpManager {
    fun sendEmail(context: Context, smsText: String) {

        var dataUtils = DataUtils()
        var host = dataUtils.loadSmtpData(context, dataUtils.SMTP_HOST)
        var port = dataUtils.loadSmtpData(context, dataUtils.SMTP_PORT)
        var email = dataUtils.loadSmtpData(context, dataUtils.SMTP_EMAIL)
        var pass = dataUtils.loadSmtpData(context, dataUtils.SMTP_PASS)

        try{
            port.toInt()
        }
        catch (e: Exception) {
            port = "0"
        }


        val userName =  email
        val password =  pass

        val emailFrom = email
        val emailTo = email
        //val emailCC = "chris_jackson_777@hotmail.com"

        val subject = "SMTP Test"
        val text = smsText

        val props = Properties()
        /*
        putIfMissing(props, "mail.smtp.host", "smtp.office365.com")
        putIfMissing(props, "mail.smtp.port", "587")
        putIfMissing(props, "mail.smtp.auth", "true")
        putIfMissing(props, "mail.smtp.starttls.enable", "true")
        */

        putIfMissing(props, "mail.smtp.host", host)
        putIfMissing(props, "mail.smtp.port", port)
        putIfMissing(props, "mail.smtp.auth", "true")
        putIfMissing(props, "mail.smtp.starttls.enable", "true")

        props.put("mail.smtp.timeout", "15000");
        props.put("mail.smtp.connectiontimeout", "15000");

        val session = Session.getInstance(props, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(userName, password)
            }
        })

        session.debug = true

        try {
            val mimeMessage = MimeMessage(session)
            mimeMessage.setFrom(InternetAddress(emailFrom))
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTo, false))
            //mimeMessage.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailCC, false))
            mimeMessage.setText(text)
            mimeMessage.subject = subject
            mimeMessage.sentDate = Date()

            val smtpTransport = session.getTransport("smtp")
            smtpTransport.connect()
            smtpTransport.sendMessage(mimeMessage, mimeMessage.allRecipients)
            smtpTransport.close()

            Log.d("SMTP", "E-Mail Sent")
        } catch (messagingException: MessagingException) {
            messagingException.printStackTrace()
            throw messagingException
        }
    }

    private fun putIfMissing(props: Properties, key: String, value: String) {
        if (!props.containsKey(key)) {
            props[key] = value
        }
    }
}
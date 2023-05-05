package com.example.smstoemail

import android.content.Context
import android.util.Log
import java.util.*
import javax.mail.*
import javax.mail.internet.*

class SmtpManager {
    fun sendEmail(context: Context, smsText: String) {

        val dataUtils = DataUtils()
        val host = dataUtils.loadSmtpData(context, dataUtils.smtpHost)
        var port = dataUtils.loadSmtpData(context, dataUtils.smtpPort)
        val email = dataUtils.loadSmtpData(context, dataUtils.smtpEmail)
        val toEmail = dataUtils.loadSmtpData(context, dataUtils.smtpToEmail)
        val pass = dataUtils.loadSmtpData(context, dataUtils.smtpPass)

        try{
            port.toInt()
        }
        catch (e: Exception) {
            port = "0"
        }


        val userName =  email
        val password =  pass

        val emailFrom = email
        val emailTo = toEmail
        //val emailCC = "chris_jackson_777@hotmail.com"

        val subject = "SMTP Test"

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

        props["mail.smtp.timeout"] = "15000"
        props["mail.smtp.connectiontimeout"] = "15000"

        val session = Session.getInstance(props, object : Authenticator() {
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
            mimeMessage.setText(smsText)
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
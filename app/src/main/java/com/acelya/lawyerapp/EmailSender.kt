import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailSender {
    private const val SMTP_HOST = "smtp.gmail.com"
    private const val SMTP_PORT = "465"
    private const val SENDER_EMAIL = "acelyayildirim2020@gmail.com"
    private const val SENDER_PASSWORD = "mbtgzxyopkuarbtk"

    fun sendEmail(to: String, subject: String, messageText: String, callback: (Boolean, String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val props = Properties()
                props["mail.smtp.auth"] = "true"
                props["mail.smtp.host"] = SMTP_HOST
                props["mail.smtp.port"] = SMTP_PORT
                props["mail.smtp.socketFactory.port"] = SMTP_PORT
                props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"


                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD)
                    }
                })

                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(SENDER_EMAIL))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                    this.subject = subject
                    setText(messageText)
                }

                Transport.send(message)
                withContext(Dispatchers.Main) {
                    callback(true, null)
                }
            } catch (e: MessagingException) {
                withContext(Dispatchers.Main) {
                    callback(false, e.message)
                }
            }
        }
    }
}

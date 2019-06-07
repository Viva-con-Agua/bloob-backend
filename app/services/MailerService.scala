package services

import play.api.libs.mailer._
import java.io.File
import org.apache.commons.mail.EmailAttachment
import javax.inject.Inject
import models.{Email => FrontendEmail}

class MailerService @Inject() (mailerClient: MailerClient) {

  def sendEmail(sendMe: FrontendEmail) = {
    val cid = "1234"
    val email = Email(
      sendMe.subject,
      sendMe.senderName+" <bloobtest@mail.de>",
      Seq("Miss TO <bloobtest@mail.de>"),
      // adds attachment
      attachments = Seq(
        // AttachmentFile("attachment.pdf", new File("/some/path/attachment.pdf")),
        // adds inline attachment from byte array
        AttachmentData("data.txt", "data".getBytes, "text/plain", Some("Simple data"), Some(EmailAttachment.INLINE)),
        // adds cid attachment
        // AttachmentFile("image.jpg", new File("/some/path/image.jpg"), contentId = Some(cid))
      ),
      // sends text, HTML or both...
      bodyText = Some("A text message"),
//      bodyHtml = Some(s"""<html><body><p>An <b>html</b> message with cid <img src="cid:$cid"></p></body></html>""")
      bodyHtml = Some(sendMe.messageData)
    )
    mailerClient.send(email)
  }

}
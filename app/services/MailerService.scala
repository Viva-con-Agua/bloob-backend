package services

import java.util.UUID
import play.api.libs.mailer._
import play.api.libs.ws.WSClient
import java.io.File
import org.apache.commons.mail.EmailAttachment
import javax.inject.Inject
import models.{Email => FrontendEmail}

class MailerService @Inject() (implicit ws: WSClient, mailerClient: MailerClient) {
/*
  def getEmailAddress(userIds: List[UUID]) {

    //TODO: get from config
    val path = drops.url.base="http://localhost/drops/rest/users";
    val client_id = "";
    val client_secret = "";


    ws.url(path)
      .addQueryStringParameters("client_id" -> client_id, "client_secret" -> client_secret)
      .post(build post with userIds)
      .map(do something with the result);
  }
*/




  def sendEmail(sendMe: FrontendEmail) = {

    //TODO: get email addresses of recievers from drops with their uuid


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
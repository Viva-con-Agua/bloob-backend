package services

import javax.inject._

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.ws.WSClient
import play.api.libs.mailer._
import play.api.Configuration
import play.libs.ws._

import java.util.UUID
import java.io.File

import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import org.apache.commons.mail.EmailAttachment

import models.{Email => FrontendEmail}

class MailerService @Inject() (implicit ws: WSClient, mailerClient: MailerClient,  config: Configuration) {

  val path : String = config.get[String]("drops.rest.base") + config.get[String]("drops.rest.user.path")
  val client_id = config.get[String]("drops.client_id")
  val client_secret = config.get[String]("drops.client_secret")

/**
* Requests the user data from drops for the given uuids 
* Returns email addresses for found users in a Seq[Option[String]] 
*/
  def getEmailAddress(userIds: List[UUID]): Seq[Option[String]] = {

    var myresult=Await.result(
    ws.url(path)
      .addHttpHeaders("Content-Type" -> "application/json")
      .addQueryStringParameters("client_id" -> client_id, "client_secret" -> client_secret)
      .post(getEmailQuery(userIds))
    ,10 seconds
    ).body

    var requestedEmails = (Json.parse(myresult) \\ "profiles").map ( x => 
      ( (x(0) \ "email" ) ).get
    )

    var myMailsString = requestedEmails.map {json => json.asOpt[String]}

    //requestedEmails.map(println(_))

    return myMailsString
  }

/**
* Returns a String that represents a valid request body. It has to be used to request the users from Drops represented by the given UUIDs.
*/
  def getEmailQuery(userIds: List[UUID]): String = {
    Json.obj(
        "query" -> userIds.indices.map("user.publicId." + _ + ".=").mkString("_OR_"),
        "values" -> Json.obj("user" -> Json.obj(
          "publicId" -> userIds.zipWithIndex.map(_.swap).foldLeft[JsObject](Json.obj())((json, i_id) =>
            json ++ Json.obj(i_id._1.toString -> Json.toJson(i_id._2))
          )
        ))
      ).toString()
  }

  def sendEmail(sendMe: FrontendEmail) = {

    var recipientsIds = sendMe.recipients.map (UUID.fromString(_))
    var recipientsAddresses = getEmailQuery(recipientsIds.toList)

    val cid = "1234"
    val email = Email(
      sendMe.subject,   //subject
      sendMe.senderName+" <bloobtest@mail.de>",   //sender
      Seq("Miss TO <bloobtest@mail.de>"),   //receiver
      //recipientsAddresses,
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
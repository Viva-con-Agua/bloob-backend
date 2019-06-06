package models

import com.google.inject.Inject
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

//frontend model
case class Email(
  id: Long,
  senderUUID: String,
  senderName: String, 
  senderMail: String,
  recipients: Array[String],
  subject: String,  
  messageData: String
)

object Email {
  implicit val emailFormat = Json.format[Email]
}

case class EmailRecipient(
  id: Long,
  emailID: Long,
  recipientUUID: String
)

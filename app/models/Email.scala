package models

import com.google.inject.Inject
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import java.sql.Date


case class Crew(
  id: String,
  name: String,
  country: Option[String],
  cities: Option[Array[String]]
)

object Crew {
  implicit val crewFormat = Json.format[Crew]
}

//frontend model
case class Email(
  id: Long,
  senderUUID: String,
  senderName: String, 
  senderMail: String,
  senderCrew: Crew,
  recipients: Seq[String],
  subject: String,  
  messageData: String,
  date: Option[Date],
  status: String
)

object Email {
  implicit val emailFormat = Json.format[Email]
}

case class EmailRecipient(
  id: Long,
  emailID: Long,
  recipientUUID: String
)

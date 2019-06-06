package models

import com.google.inject.Inject
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}


case class EmailAccessRight(
  id: Long, 
  roleName: String, 
  pillar: String, 
  crewName: String, 
  email: String
  )

object EmailAccessRight {
  implicit val emailARFormat = Json.format[EmailAccessRight]
}

case class EmailARRequest(
  roleName: Array[String],
  pillar: String,
  crewName: String
)
object EmailARRequest {
  implicit val emailARRequestFormat = Json.format[EmailARRequest]
}

case class EmailARDeleteRequest(
  id: Long
)
object EmailARDeleteRequest {
  implicit val emailARDeleteRequest = Json.format[EmailARDeleteRequest]
}

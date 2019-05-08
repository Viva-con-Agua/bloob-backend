package models

import com.google.inject.Inject
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class EmailAR(id: Long, roleName: String, crewName: String, email: String)

object EmailAR {
  implicit val emailARFormat = Json.format[EmailAR]
}

case class EmailARFormData(roleName: String, crewName: String, email: String)

object EmailARForm {

  val form = Form(
    mapping(
      "roleName" -> nonEmptyText,
      "crewName" -> text,
      "email" -> email
    )(EmailARFormData.apply)(EmailARFormData.unapply)
  )
}
//ab hier bis ende in extra DAO package auslagern
import slick.jdbc.MySQLProfile.api._

class EmailARTableDef(tag: Tag) extends Table[EmailAR](tag, "emailAR") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def roleName = column[String]("role_name")
  def crewName = column[String]("crew_name")
  def email = column[String]("email")

  override def * =
    (id, roleName, crewName, email) <>((EmailAR.apply _).tupled, EmailAR.unapply)
}

/**
 * Database-Access-Object (DAO)
 **/
class EmailARs @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  val emailARs = TableQuery[EmailARTableDef]

  def add(emailAR: EmailAR): Future[Option[EmailAR]] = {
    dbConfig.db.run(emailARs += emailAR).flatMap(id => get(id))
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(emailARs.filter(_.id === id).delete)
  }

  def get(id: Long): Future[Option[EmailAR]] = {
    dbConfig.db.run(emailARs.filter(_.id === id).result.headOption)
  }

  def listAll: Future[Seq[EmailAR]] = {
   dbConfig.db.run(emailARs.result)
  }

/*  def getByRole(role: String, crew: Option[String]): Future[Set[EmailAR]] = {
    dbConfig.db.run(emailARs.filter(entry => entry.roleName === role && (crew.isEmpty || entry.crewName === crew.get)).result.toSet)
  }*/
  def getByRole(role: String): Future[Seq[EmailAR]] = {
    dbConfig.db.run(emailARs.filter(entry => entry.roleName === role).result)
  }
  
  }

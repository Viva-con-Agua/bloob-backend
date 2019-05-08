package models

import com.google.inject.Inject
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class User(id: Long, roleName: String, crewName: String, email: String)

object User {
  implicit val userFormat = Json.format[User]
}

case class UserFormData(roleName: String, crewName: String, email: String)

object UserForm {

  val form = Form(
    mapping(
      "roleName" -> nonEmptyText,
      "crewName" -> text,
      "email" -> email
    )(UserFormData.apply)(UserFormData.unapply)
  )
}
//ab hier bis ende in extra DAO package auslagern
import slick.jdbc.MySQLProfile.api._

class UserTableDef(tag: Tag) extends Table[User](tag, "user") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def roleName = column[String]("role_name")
  def crewName = column[String]("crew_name")
  def email = column[String]("email")

  override def * =
    (id, roleName, crewName, email) <>((User.apply _).tupled, User.unapply)
}

/**
 * Database-Access-Object (DAO)
 **/
class Users @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  val users = TableQuery[UserTableDef]

  def add(user: User): Future[Option[User]] = {
    dbConfig.db.run(users += user).flatMap(id => get(id))
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(users.filter(_.id === id).delete)
  }

  def get(id: Long): Future[Option[User]] = {
    dbConfig.db.run(users.filter(_.id === id).result.headOption)
  }

  def listAll: Future[Seq[User]] = {
   dbConfig.db.run(users.result)
  }

/*  def getByRole(role: String, crew: Option[String]): Future[Set[User]] = {
    dbConfig.db.run(users.filter(entry => entry.roleName === role && (crew.isEmpty || entry.crewName === crew.get)).result.toSet)
  }*/
  def getByRole(role: String): Future[Seq[User]] = {
    dbConfig.db.run(users.filter(entry => entry.roleName === role).result)
  }
  
  }

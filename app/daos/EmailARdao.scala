package daos

import com.google.inject.Inject
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

import models.{EmailAR, EmailARRequest}

//ab hier bis ende in extra DAO package auslagern
import slick.jdbc.MySQLProfile.api._

class EmailARTableDef(tag: Tag) extends Table[EmailAR](tag, "emailAR") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def roleName = column[String]("role_name")
  def pillar = column[String]("pillar")
  def crewName = column[String]("crew_name")
  def email = column[String]("email")

  override def * =
    (id, roleName, pillar, crewName, email) <>((EmailAR.apply _).tupled, EmailAR.unapply)
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

  def getByRequest(request: EmailARRequest): Future[Seq[String]] = {
    var q = emailARs.filter(entry => 
      entry.roleName === request.roleName(0) 
      && (entry.crewName === request.crewName || entry.crewName === "")
      && (entry.pillar === request.pillar || entry.pillar === "")
    ).map(_.email)
    
    for(n <- request.roleName){
      q = (q union emailARs.filter(entry => 
        entry.roleName === n 
        && (entry.crewName === request.crewName || entry.crewName === "")
        && (entry.pillar === request.pillar || entry.pillar === "")
      ).map(_.email))

    }
    dbConfig.db.run(q.result)
  }
 
}

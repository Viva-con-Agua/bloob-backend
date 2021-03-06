package daos

import com.google.inject.Inject
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import java.sql.Date

import models.{Email, EmailRecipient}
import daos.reader.EmailReader

import slick.jdbc.MySQLProfile.api._

class EmailTableDef(tag: Tag) extends Table[EmailReader](tag, "email") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def senderUUID = column[String]("sender_uuid")
  def senderName = column[String]("sender_role")
  def senderMail = column[String]("sender_mail")
  def senderCrewName = column[String]("sender_crew_name")
  def senderCrewId = column[String]("sender_crew_id")
  def subject = column[String]("subject")
  def messageData = column[String]("message_data")
  def date = column[Date]("date")
  def status = column[String]("status")

  override def * =
    (id, senderUUID, senderName, senderCrewName, senderCrewId, senderMail, subject, messageData, date, status) <>((EmailReader.apply _).tupled, EmailReader.unapply)
}

class EmailRecipientsTableDef(tag: Tag) extends Table[EmailRecipient](tag, "email_recipient") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def emailID = column[Long]("email_id")
  def recipientUUID = column[String]("recipient_uuid")

  override def * =
    (id, emailID, recipientUUID) <>((EmailRecipient.apply _).tupled, EmailRecipient.unapply)
}

class EmailDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  val emails = TableQuery[EmailTableDef]
  val emailRecipients = TableQuery[EmailRecipientsTableDef]
  import EmailReader._

  /*val innerJoin = for {
    (email, recipients) <- emails join emailRecipients on (_.id === _.email_id)
  } yield*/

  def get(id: Long): Future[Option[EmailReader]] = {
    dbConfig.db.run(emails.filter(_.id === id).result.headOption)
  }

  def insertEmail(email: EmailReader, recipients: Seq[String]): Future[Option[EmailReader]] = {   
  
    val insertEmailAction = for {
        newEmailID <- (emails returning emails.map(_.id) += email)
        newResult <- emailRecipients ++= recipients.map(r => EmailRecipient(-1, newEmailID, r))
        newEmail <- emails.filter(_.id === newEmailID).result.headOption
    } yield newEmail
 
    dbConfig.db.run(insertEmailAction)
  }

  def getAllMails: Future[Seq[EmailReader]] = {
    dbConfig.db.run(emails.result)
  }

  def getMyMails(uuid: String): Future[Seq[EmailReader]] = {
    var q = emails.filter(entry =>
    entry.senderUUID === uuid)
    dbConfig.db.run(q.result)
  }

  def getRecipients(emailID: Long): Future[Seq[String]] = {
    var q = emailRecipients.filter(entry => 
      entry.emailID === emailID).map(_.recipientUUID)
    dbConfig.db.run(q.result)
  }
/*
  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(emails.filter(_.id === id).delete)
  } 
*/
}

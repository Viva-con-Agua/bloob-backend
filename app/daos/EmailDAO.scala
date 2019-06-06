package daos

import com.google.inject.Inject
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

import models.{Email, EmailRecipient}
import daos.reader.EmailReader

import slick.jdbc.MySQLProfile.api._

class EmailTableDef(tag: Tag) extends Table[EmailReader](tag, "email") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def senderUUID = column[String]("sender_uuid")
  def senderRole = column[String]("sender_role")
  def senderMail = column[String]("sender_mail")
  def subject = column[String]("subject")
  def messageData = column[String]("message_data")

  override def * =
    (id, senderUUID, senderRole, senderMail, subject, messageData) <>((EmailReader.apply _).tupled, EmailReader.unapply)
}

class EmailRecipientsTableDef(tag: Tag) extends Table[EmailRecipient](tag, "email_recipient") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def emailID = column[Long]("email_id")
  def recipientUUID = column[Long]("recipient_uuid")

  override def * =
    (id, emailID, recipientUUID) <>((EmailRecipient.apply _).tupled, EmailRecipient.unapply)
}

class EmailDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  val emails = TableQuery[EmailTableDef]
  val emailRecipients = TableQuery[EmailRecipientsTableDef]
  
  /*val innerJoin = for {
    (email, recipients) <- emails join emailRecipients on (_.id === _.email_id)
  } yield*/
/*
  def add(email: Email): Future[Option[Email]] = {
    dbConfig.db.run(emails += email).flatMap(id => get(id))
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(emails.filter(_.id === id).delete)
  } 
*/
}

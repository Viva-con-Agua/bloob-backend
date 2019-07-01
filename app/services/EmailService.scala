package services

import com.google.inject.Inject
import models.Email
import daos.EmailDAO
import daos.reader.EmailReader
import java.sql.Date
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._
import scala.util.{Try, Success, Failure}

import scala.concurrent.Future

class EmailService @Inject() (emailDAO: EmailDAO) {

  def insertEmail(email: Email): Future[Option[EmailReader]] = {
    var date = new Date(System.currentTimeMillis())
    print("Date: ")
    println(date)
    var emailEntry = EmailReader(email.id, email.senderUUID, email.senderName, email.senderMail, email.senderCrew.name, email.senderCrew.id, email.subject, email.messageData, date, email.status)
    //var emailEntry = (EmailReader.apply _).tupled(email.tupled)
    println("building emailEntry")
    return emailDAO.insertEmail(emailEntry, email.recipients)
  }
  def getAllMails(): Future[Seq[EmailReader]] = {
    emailDAO.getAllMails
  }
  def getAllMailsFull(): Future[Seq[Email]] = {
    implicit val ec = ExecutionContext.global
    var emailReadersFuture = emailDAO.getAllMails;
    println(emailReadersFuture);
    var emails = Seq.empty[Email];

    var emailReaders = Await.result(emailReadersFuture, 10 seconds)
        // println("waited for emailReaders future")
        println(emailReaders);
        emails = emailReaders.map( x => {
          var recipientsFuture = emailDAO.getRecipients(x.id);
          var recipients = Await.result(recipientsFuture, 10 seconds)
          x.toEmail(recipients)
          
        })
    println("sending emails to frontend")
    println(emails)
    return Future(emails);
  }
  def getMyMailsFull(uuid: String): Future[Seq[Email]] = {
    implicit val ec = ExecutionContext.global
    var emailReadersFuture = emailDAO.getMyMails(uuid);
    // TODO: get mails that i have received too
    println(emailReadersFuture);
    var emails = Seq.empty[Email];

    var emailReaders = Await.result(emailReadersFuture, 10 seconds)
        println(emailReaders);
        emails = emailReaders.map( x => {
          var recipientsFuture = emailDAO.getRecipients(x.id);
          var recipients = Await.result(recipientsFuture, 10 seconds)
          x.toEmail(recipients)
          
        })
    println("sending emails to frontend")
    println(emails)
    return Future(emails);
  }
}

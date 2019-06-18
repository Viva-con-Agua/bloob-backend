package services

import com.google.inject.Inject
import models.Email
import daos.EmailDAO
import daos.reader.EmailReader
import java.sql.Date
import scala.concurrent.{ExecutionContext, Future}
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
  def getAllMailsFull(): Future[Array[Email]] = {
    implicit val ec = ExecutionContext.global
    var emailReadersFuture = emailDAO.getAllMails;
    var emails = Array.empty[Email];
    //println(emailReadersFuture);

    emailReadersFuture.onComplete({
      case Success(emailReaders) => {
        emailReaders.foreach {
          case emailReader => {
            //println("hallo im foreach")
            //println(emailReader)
            emails :+ emailReader.toEmail
            emailDAO.getRecipients(emailReader.id).onComplete({
              case Success(recipients) => {
                emails.last.recipients = recipients
              }
              case Failure(exception) => {
                println("error getting recipients")
                println(exception)
              }
            })
          }
        }
      }
      case Failure(exception) => {
        println("error:")
        println(exception)
      }
    })
    return emails

    /*
    for(n <- emailReaders){
      println(n);
    //  var emailtemp = n.toEmail;
    //  emailTemp.recipients = emailDAO.getRecipients(n.emailID);
    //  emails :+ emailTemp;
    }
*/
    return Future(emails);
  }
/*
  def deleteEmail(id: Long): Future[Int] = {
    emailDAO.delete(id)
  }*/
}

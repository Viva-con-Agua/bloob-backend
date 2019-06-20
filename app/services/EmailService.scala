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

    //emailReadersFuture.onComplete({
    var emailReaders = Await.result(emailReadersFuture, 10 seconds)
    //  case Success(emailReaders) => {
        println("waited for emailReaders future")
        println(emailReaders);
        emails = emailReaders.map( x => {
          var recipientsFuture = emailDAO.getRecipients(x.id);
          var recipients = Await.result(recipientsFuture, 10 seconds)
          x.toEmail(recipients)
          
        })
    //  }
    
      
        
//        println(emails)
//        println(emails.last)

        /*emailReaders.foreach {
          case emailReader => {
            println("foreach emailReader");
            //println(emailReader)
            emails :+ emailReader.toEmail;
            println(emails);
            println(emails.last)
            var recipientsFuture = emailDAO.getRecipients(emailReader.id);
            recipientsFuture.onComplete({
              case Success(recipients) => {
                println("all recipients");
                println(recipients);
                recipients.foreach {
                  case recipient => {
                    println("foreach recipient");
                    println(recipient);
                    emails.last.recipients :+ recipient;
                  }
                }
              }
              case Failure(exception) => {
                println("error getting recipients")
                println(exception)
              }
            })
          }
        }*/
     /* 
      case Failure(exception) => {
        println("error:")
        println(exception)
      }
    })*/
    println("returning")
    println(emails)
    return Future(emails);
    
    /*
    for(n <- emailReaders){
      println(n);
    //  var emailtemp = n.toEmail;
    //  emailTemp.recipients = emailDAO.getRecipients(n.emailID);
    //  emails :+ emailTemp;
    }
*/
  }
/*
  def deleteEmail(id: Long): Future[Int] = {
    emailDAO.delete(id)
  }*/
}

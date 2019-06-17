package services

import com.google.inject.Inject
import models.Email
import daos.EmailDAO
import daos.reader.EmailReader
import java.sql.Date

import scala.concurrent.Future

class EmailService @Inject() (emailDAO: EmailDAO) {

  def insertEmail(email: Email): Future[Option[EmailReader]] = {
    var date = new Date(System.currentTimeMillis())
    println("Date")
    print(date)
    var emailEntry = EmailReader(email.id, email.senderUUID, email.senderName, email.senderMail, email.senderCrew.name, email.senderCrew.id, email.subject, email.messageData, date, email.status)
    //var emailEntry = (EmailReader.apply _).tupled(email.tupled)
    println("building emailEntry")
    return emailDAO.insertEmail(emailEntry, email.recipients)
  }
  def getAllMails(): Future[Seq[EmailReader]] = {
    emailDAO.getAllMails
  }
/*
  def deleteEmail(id: Long): Future[Int] = {
    emailDAO.delete(id)
  }*/
}

package services

import com.google.inject.Inject
import models.Email
import daos.EmailDAO
import daos.reader.EmailReader

import scala.concurrent.Future

class EmailService @Inject() (emailDAO: EmailDAO) {

  def insertEmail(email: Email): Future[Option[EmailReader]] = {
    var emailEntry = EmailReader(email.id, email.senderUUID, email.senderName, email.senderMail, email.subject, email.messageData)
    println("building emailEntry")
    return emailDAO.insertEmail(emailEntry, email.recipients)
  }
/*
  def deleteEmail(id: Long): Future[Int] = {
    emailDAO.delete(id)
  }*/
}

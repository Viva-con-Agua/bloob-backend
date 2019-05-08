package services

import com.google.inject.Inject
import models.{EmailAR, EmailARs}

import scala.concurrent.Future

class EmailARService @Inject() (emailARs: EmailARs) {

  def addEmailAR(emailAR: EmailAR): Future[Option[EmailAR]] = {
    emailARs.add(emailAR)
  }

  def deleteEmailAR(id: Long): Future[Int] = {
    emailARs.delete(id)
  }

  def getEmailAR(id: Long): Future[Option[EmailAR]] = {
    emailARs.get(id)
  }

  def listAllEmailARs(): Future[Seq[EmailAR]] = {
    emailARs.listAll
  }
  def getByRole(roleName: String): Future[Seq[EmailAR]] = {
    emailARs.getByRole(roleName)
  }
}

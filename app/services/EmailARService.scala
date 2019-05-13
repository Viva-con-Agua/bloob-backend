package services

import com.google.inject.Inject
import models.{EmailAR, EmailARs, EmailARRequest}

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
  def getByRole(roleName: String): Future[Seq[String]] = {
    emailARs.getByRole(roleName)
  }
  def getByRequest(request: EmailARRequest): Future[Seq[String]] = {
    emailARs.getByRequest(request)
  }
}

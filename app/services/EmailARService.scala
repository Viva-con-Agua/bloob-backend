package services

import com.google.inject.Inject
import models.{EmailAR, EmailARRequest}
import daos.EmailARdao

import scala.concurrent.Future

class EmailARService @Inject() (emailARdao: EmailARdao) {

  def addEmailAR(emailAR: EmailAR): Future[Option[EmailAR]] = {
    emailARdao.add(emailAR)
  }

  def deleteEmailAR(id: Long): Future[Int] = {
    emailARdao.delete(id)
  }

  def getEmailAR(id: Long): Future[Option[EmailAR]] = {
    emailARdao.get(id)
  }

  def listAllEmailARs(): Future[Seq[EmailAR]] = {
    emailARdao.listAll
  }
  
  def getByRequest(request: EmailARRequest): Future[Seq[String]] = {
    emailARdao.getByRequest(request)
  }
}

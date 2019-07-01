package services

import com.google.inject.Inject
import models.{EmailAccessRight, EmailARRequest}
import daos.EmailAccessRightDAO

import scala.concurrent.Future

class EmailAccessRightsService @Inject() (emailARdao: EmailAccessRightDAO) {

  def addEmailAR(emailAccessRights: EmailAccessRight): Future[Option[EmailAccessRight]] = {
    emailARdao.add(emailAccessRights)
  }

  def deleteEmailAR(id: Long): Future[Int] = {
    emailARdao.delete(id)
  }

  def getEmailAR(id: Long): Future[Option[EmailAccessRight]] = {
    emailARdao.get(id)
  }

  def listAllEmailARs(): Future[Seq[EmailAccessRight]] = {
    emailARdao.listAll
  }
  
  def getByRequest(request: EmailARRequest): Future[Seq[String]] = {
    emailARdao.getByRequest(request)
  }
}

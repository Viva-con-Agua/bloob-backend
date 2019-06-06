package services

import com.google.inject.Inject
import models.{EmailAccessRight, EmailARRequest}
import daos.EmailDAO

import scala.concurrent.Future

class EmailService @Inject() (emailDAO: EmailDAO) {
/*
  def addEmail(email: Email): Future[Option[Email]] = {
    emailDAO.add(email)
  }

  def deleteEmail(id: Long): Future[Int] = {
    emailDAO.delete(id)
  }*/
}

package services

import com.google.inject.Inject
import models.{User, Users}

import scala.concurrent.Future

class UserService @Inject() (users: Users) {

  def addUser(user: User): Future[Option[User]] = {
    users.add(user)
  }

  def deleteUser(id: Long): Future[Int] = {
    users.delete(id)
  }

  def getUser(id: Long): Future[Option[User]] = {
    users.get(id)
  }

  def listAllUsers(): Future[Seq[User]] = {
    users.listAll
  }
  def getByRole(roleName: String): Future[Seq[User]] = {
    users.getByRole(roleName)
  }
}
package controllers

import javax.inject._
import models.{User, UserForm}
import play.api.Logging
import play.api.mvc._
import services.UserService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ApplicationController @Inject()(cc: ControllerComponents, userService: UserService) extends AbstractController(cc) with Logging {

  def db() = Action.async { implicit request: Request[AnyContent] =>
    userService.listAllUsers map { users =>
      Ok(views.html.db(UserForm.form, users))
    }
  }

  def addUser() = Action.async { implicit request: Request[AnyContent] =>
    UserForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => {
        logger.warn(s"Form submission with error: ${errorForm.errors}")
        Future.successful(Ok(views.html.db(errorForm, Seq.empty[User])))
      },
      data => {
        val newUser = User(0, data.firstName, data.lastName, data.mobile, data.email)
        userService.addUser(newUser).map( _ => Redirect(routes.ApplicationController.db()))
      })
  }

  def deleteUser(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    userService.deleteUser(id) map { res =>
      Redirect(routes.ApplicationController.db())
    }
  }

}

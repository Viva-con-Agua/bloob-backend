package controllers

import javax.inject._
import models.{User, UserForm}
import play.api.Logging
import play.api.mvc._
import play.api.libs.json.{JsError, JsValue, Json}
import services.UserService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import scala.concurrent.{ExecutionContext, Future}


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
        val newUser = User(0, data.roleName, data.crewName, data.email)
        userService.addUser(newUser).map( _ => Redirect(routes.ApplicationController.db()))
      })
  }

  def deleteUser(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    userService.deleteUser(id) map { res =>
      Redirect(routes.ApplicationController.db())
    }
  }
  def getByRole(roleName: String) = Action.async { implicit request: Request[AnyContent] =>
    userService.getByRole(roleName) map { res =>
      Redirect(routes.ApplicationController.db())
    }
  }

  def create = Action(parse.json).async { implicit request =>
    println("parsing json")
    println(request)
    println(request.body)
    implicit val ec = ExecutionContext.global
    request.body.validate[User].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      user => {
        userService.addUser(user).map(userOption => userOption match {
          case Some(u) => Ok(Json.toJson( u ))
          case None => InternalServerError( Json.obj(
            "msg" -> "Was not able to save the data",
            "obj" -> Json.toJson(user)
          ))
        })
      }
)
  }
  def addUserFrontend(roleName: String, crewName: String, email: String) = Action.async {implicit request: Request[AnyContent] =>
    val newUser = User(0, roleName, crewName, email)
    userService.addUser(newUser).map( _ => Redirect(routes.ApplicationController.db()))
  }
  def listAllUsers() = Action.async {implicit request: Request[AnyContent] =>
  userService.listAllUsers() map { res =>
    Redirect(routes.ApplicationController.db())}
  }
}
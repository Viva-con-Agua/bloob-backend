package controllers

import javax.inject._
import models.{EmailAR, EmailARForm}
import play.api.Logging
import play.api.mvc._
import play.api.libs.json.{JsError, JsValue, Json}
import services.EmailARService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ApplicationController @Inject()(cc: ControllerComponents, emailARService: EmailARService) extends AbstractController(cc) with Logging {

  def db() = Action.async { implicit request: Request[AnyContent] =>
    emailARService.listAllEmailARs map { emailARs =>
      Ok(views.html.db(EmailARForm.form, emailARs))
    }
  }

  def addEmailAR() = Action.async { implicit request: Request[AnyContent] =>
    EmailARForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => {
        logger.warn(s"Form submission with error: ${errorForm.errors}")
        Future.successful(Ok(views.html.db(errorForm, Seq.empty[EmailAR])))
      },
      data => {
        val newEmailAR = EmailAR(0, data.roleName, data.crewName, data.email)
        emailARService.addEmailAR(newEmailAR).map( _ => Redirect(routes.ApplicationController.db()))
      })
  }

  def deleteEmailAR(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    emailARService.deleteEmailAR(id) map { res =>
      Redirect(routes.ApplicationController.db())
    }
  }
  def getByRole(roleName: String) = Action.async { implicit request: Request[AnyContent] =>
    emailARService.getByRole(roleName) map { res =>
      Redirect(routes.ApplicationController.db())
    }
  }

  def create = Action(parse.json).async { implicit request =>
    println(request)
    println(request.body)
    implicit val ec = ExecutionContext.global
    request.body.validate[EmailAR].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      emailAR => {
        emailARService.addEmailAR(emailAR).map(emailAROption => emailAROption match {
          case Some(u) => Ok(Json.toJson( u ))
          case None => InternalServerError( Json.obj(
            "msg" -> "Was not able to save the data",
            "obj" -> Json.toJson(emailAR)
          ))
        })
      }
)
  }
  def addEmailARFrontend(roleName: String, crewName: String, email: String) = Action.async {implicit request: Request[AnyContent] =>
    val newEmailAR = EmailAR(0, roleName, crewName, email)
    emailARService.addEmailAR(newEmailAR).map( _ => Redirect(routes.ApplicationController.db()))
  }
  def listAllEmailARs() = Action.async {implicit request: Request[AnyContent] =>
    emailARService.listAllEmailARs map { emailARs =>
      Ok(Json.toJson(emailARs))
    }
  }
}

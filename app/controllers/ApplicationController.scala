package controllers

import javax.inject._
import models.{EmailAccessRight, EmailARRequest, EmailARDeleteRequest, Email}
import play.api.Logging
import play.api.mvc._
import play.api.libs.json.{JsError, JsValue, Json}
import services.{EmailAccessRightsService, EmailService, MailerService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ApplicationController @Inject()(cc: ControllerComponents, emailARService: EmailAccessRightsService, emailService: EmailService, mailerService: MailerService) extends AbstractController(cc) with Logging {

  def deleteEmailAR2 = Action(parse.json).async { implicit request =>
    //println(request)
    //println(request.body)
    implicit val ec = ExecutionContext.global
    request.body.validate[EmailARDeleteRequest].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      emailARDeleteRequest => {
        emailARService.deleteEmailAR(emailARDeleteRequest.id) map { res =>
        Ok(Json.toJson(emailARDeleteRequest.id))
        }
      }
    )
  }

  def getByRole2 = Action(parse.json).async { implicit request =>
    println(request)
    println(request.body)
    implicit val ec = ExecutionContext.global
    request.body.validate[EmailARRequest].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      emailARRequest => {
        emailARService.getByRequest(emailARRequest) map { emails =>
        println("sending response: "+emails)
        Ok(Json.toJson(emails))
        }
      }
    )
  }

  def create = Action(parse.json).async { implicit request =>
    //println(request)
    //println(request.body)
    implicit val ec = ExecutionContext.global
    request.body.validate[EmailAccessRight].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      emailAccessRights => {
        emailARService.addEmailAR(emailAccessRights).map(emailAROption => emailAROption match {
          case Some(u) => Ok(Json.toJson( u ))
          case None => InternalServerError( Json.obj(
            "msg" -> "Was not able to save the data",
            "obj" -> Json.toJson(emailAccessRights)
          ))
        })
      }
    )
  }
  
  def listAllEmailARs() = Action.async {implicit request: Request[AnyContent] =>
    emailARService.listAllEmailARs map { emailARs =>
      Ok(Json.toJson(emailARs))
    }
  }
  def sendMail() = Action(parse.json).async  { implicit request =>
    println(request)
    println(request.body)
    implicit val ec = ExecutionContext.global
    val validEmail = request.body.validate[Email]
    validEmail.fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      email => {
        println("validated")
        mailerService.sendEmail(email)
        emailService.insertEmail(email).map(emailOption => emailOption match {
          case Some(u) => Ok(Json.toJson( u ))
          case None => InternalServerError( Json.obj(
            "msg" -> "Was not able to save the data",
            "obj" -> Json.toJson(email)
          ))
        })
      }
    )
  }
}

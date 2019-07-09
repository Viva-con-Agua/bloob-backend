package controllers

import javax.inject._

import play.api.Logging
import play.api.mvc._
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.Configuration

import java.util.UUID

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

import com.mohiva.play.silhouette.api.Silhouette
import org.vivaconagua.play2OauthClient.silhouette.{CookieEnv, UserService}
import org.vivaconagua.play2OauthClient.drops.authorization._

import models.{EmailAccessRight, EmailARRequest, EmailARDeleteRequest, Email}
import services.{EmailAccessRightsService, EmailService, MailerService}

@Singleton
class ApplicationController @Inject()(
  cc: ControllerComponents,
  silhouette: Silhouette[CookieEnv],
  userService: UserService,
  emailARService: EmailAccessRightsService,
  emailService: EmailService,
  mailerService: MailerService,
  config: Configuration,
) extends AbstractController(cc) with Logging {

  val path : String = config.get[String]("drops.rest.base") + config.get[String]("drops.rest.user.path")
  val client_id = config.get[String]("drops.client_id")
  val client_secret = config.get[String]("drops.client_secret")

  def deleteEmailAR = silhouette.SecuredAction( 
    IsEmployee || IsAdmin )
    .async(parse.json) { implicit request =>
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

  def getByRole2 = silhouette.SecuredAction.async(parse.json) { implicit request =>
    //println(request)
    //println(request.body)
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

  def create = silhouette.SecuredAction.async(parse.json) { implicit request =>
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
  
  def listAllEmailARs() = silhouette.SecuredAction.async {implicit request: Request[AnyContent] =>
    emailARService.listAllEmailARs map { emailARs =>
      Ok(Json.toJson(emailARs))
    }
  }
  def sendMail() = silhouette.SecuredAction.async(parse.json)  { implicit request =>
    println(request)
    println(request.body)
    implicit val ec = ExecutionContext.global
    val validEmail = request.body.validate[Email]
    validEmail.fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      email => {
        println("validated")
        //mailerService.sendEmail(email)
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
  def getAllMails() = silhouette.SecuredAction.async {implicit request =>
    println("all saved emails requested")
    emailService.getAllMailsFull map { emails =>
      Ok(Json.toJson(emails))
    }
  }
  def getMyMails() = silhouette.SecuredAction.async {implicit request =>
    println("all saved emails requested")
    println("getting uuid of user: ")
    println(request.identity.uuid)
    emailService.getMyMailsFull(request.identity.uuid.toString()) map { emails =>
      Ok(Json.toJson(emails))
    }
  }
  def testDropsRest() = Action.async { implicit request =>
    
    //var aUuid = UUID.fromString("c3702bf6-9e98-4b7b-957e-261ea12c552c")
    //var aUuid = UUID.fromString("58fd2d56-fa65-4a11-a3a8-7991cadc1809")
    //var aUuid = UUID.fromString("c6621c0c-7e6b-4706-a790-8e8a49b7a603")
    
    var listUuid = List(UUID.fromString("c3702bf6-9e98-4b7b-957e-261ea12c552c"),UUID.fromString("58fd2d56-fa65-4a11-a3a8-7991cadc1809"))
    var result = mailerService.getEmailAddress(listUuid)
    println(result)
    var itsmapped = result.map( option => option.get )
    println(itsmapped)
    Future(Ok(itsmapped.toString))
  }


} //end ApplicationController

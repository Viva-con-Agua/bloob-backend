package controllers

import javax.inject._
import java.util.UUID
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.ws.WSClient
import play.libs.ws._
import utils.{Ascending, Descending, SortDir}
import play.api.Configuration
import models.{EmailAccessRight, EmailARRequest, EmailARDeleteRequest, Email}
import play.api.Logging
import play.api.mvc._
import play.api.libs.json.{JsError, JsValue, Json}
import services.{EmailAccessRightsService, EmailService, MailerService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration._

import com.mohiva.play.silhouette.api.Silhouette
import org.vivaconagua.play2OauthClient.silhouette.{CookieEnv, UserService}
import org.vivaconagua.play2OauthClient.drops.authorization._


import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ApplicationController @Inject()(
  cc: ControllerComponents,
  silhouette: Silhouette[CookieEnv],
  userService: UserService,
  emailARService: EmailAccessRightsService,
  emailService: EmailService,
  mailerService: MailerService,
  ws: WSClient,
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
    println("hello test")
    
    //var aUuid = UUID.fromString("c3702bf6-9e98-4b7b-957e-261ea12c552c")
    var aUuid = UUID.fromString("58fd2d56-fa65-4a11-a3a8-7991cadc1809")
    //var aUuid = UUID.fromString("c6621c0c-7e6b-4706-a790-8e8a49b7a603")
    
    println(aUuid)
    var listUuid = List(UUID.fromString("c3702bf6-9e98-4b7b-957e-261ea12c552c"),UUID.fromString("58fd2d56-fa65-4a11-a3a8-7991cadc1809"))
    var requestC = UserCrewRequest(listUuid)
    var requestString = requestC.toString
    println(path)
    println(requestString)
    var myresult=Await.result(
    ws.url(path)
      .addHttpHeaders("Content-Type" -> "application/json")
      .addQueryStringParameters("client_id" -> client_id, "client_secret" -> client_secret)
      .post(requestString)
    ,10 seconds
    ).body

    var myJsonResult = Json.parse(myresult)
    //var myJsonResultArray = JSArray
    //myJsonResult.map (x => println(x))
    println(myJsonResult \\ "profiles")

    var requestedEmails = (myJsonResult \\ "profiles").map ( x => 
      ( (x(0) \ "email" ) ).get
    )

    println(requestedEmails)

    //var emails = myresult.map(x => x.profiles.email)
    //myresult[0].user.profiles[0].email
    //for (x <- Json.parse(myresult)) {println(x)}
    println(Json.parse(myresult))
    //val myjson = Json.parse(myresult)(0)
    //println(myjson)
    //println("looking at \\ profiles(0)")
    //println((myjson \ "profiles")(0))
    println("-----------------------------------")
    //println("looking at \\ profiles(0) \\ email")
    //println( ( (myjson \ "profiles")(0) \ "email" ) )
    //var anEmail = ( (myjson \ "profiles")(0) \ "email" ).get

    //val myjsonList = Json.parse(myresult)

    //println("now trying with list and map")
    //println(myjsonList)
    
    /*var emailsList = myjsonList.map ( x => 
      ( (x \ "profiles")(0) \ "email" ).get
    )
    println(emailsList)
*/

    //((Json.parse(myresult) \ "suspendData") \ "d")(1)

    Future(Ok(Json.parse(myresult)))
  
  }

  /**
* Returns a JSON object that represents a valid request body. It has to be used to request the user from Drops represented by the given UUIDs.
*/
def getEmailQuery(userIds: List[UUID]): JsValue = {
  Json.obj("user" -> Json.obj(
      "publicId" -> userIds.zipWithIndex.map(_.swap).foldLeft[JsObject](Json.obj())((json, i_id) =>
        json ++ Json.obj(i_id._1.toString -> Json.toJson(i_id._2))
      )
    ))
}

  case class UserCrewRequest(userIds: List[UUID], dir: SortDir = Ascending) {
//    println(this.toString)

    /**
      * Generates a JSON string equivalent to the request body.
      *
      * @author Johann Sell
      * @return
      */
    override def toString: String = Json.obj(
      "query" -> this.toQuery,
      "values" -> this.toValues,
      "sort" -> this.toSort
    ).toString()

    /**
      * Generates the query string.
      *
      * @author Johann Sell
      * @return
      */
    def toQuery : String = userIds.indices.map("user.publicId." + _ + ".=").mkString("_OR_")

    /**
      * Generates the JSON that can be used as values attribute of a drops REST query.
      *
      * @author Johann Sell
      * @return
      */
    def toValues : JsValue = Json.obj("user" -> Json.obj(
      "publicId" -> userIds.zipWithIndex.map(_.swap).foldLeft[JsObject](Json.obj())((json, i_id) =>
        json ++ Json.obj(i_id._1.toString -> Json.toJson(i_id._2))
      )
    ))

    /**
      * Generates the JSON that can be used as sort attribute of a drops REST query.
      * @return
      */
    def toSort : JsValue =
      Json.obj("attributes" -> Json.toJson(List("SupporterCrew_name")), "dir" -> dir.name)
  }

  /**
    * Companion object for request class.
    *
    * @author Johann Sell
    */
  object UserCrewRequest {
    /**
      * Generates JSON (Writes)
      *
      * @author Johann Sell
      */
    implicit val crewRequestWrites: Writes[UserCrewRequest] = (
      (JsPath \ "query").write[String] and
        (JsPath \ "values").write[JsValue] and
        (JsPath \ "sort").write[JsValue]
    )((request: UserCrewRequest) => (request.toQuery, request.toValues, request.toSort))
  }

    /**
    * Represents a Drops REST response.
    *
    * @author Johann Sell
    * @param id
    * @param crew
    */
  case class UserResponse(id: UUID, crew: Option[UUID])

  /**
    * Companion object for a Drops REST repsonse.
    *
    * @author Johann Sell
    */
  object UserResponse {
//    implicit val userResponseFormat = Json.format[UserResponse]
    implicit val userResponseReads : Reads[UserResponse] = (
      (JsPath \ "id").read[UUID] and
        (JsPath \ "profiles" \\ "supporter" \ "crew" \ "id").readNullable[UUID]
    )((userId, crewIds) => UserResponse(userId, crewIds))
}
  
//end
}

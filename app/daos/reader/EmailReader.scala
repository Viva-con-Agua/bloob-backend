package daos.reader

import models.Email
import slick.jdbc.GetResult
import play.api.libs.json._

case class EmailReader(
  id: Long,
  senderUUID: String,
  senderName: String, 
  senderMail: String,
  subject: String,  
  messageData: String
  ) {
    def toEmail: Email = Email(  
      id,
      senderUUID,
      senderName, 
      senderMail,
      Array[String](),
      subject,  
      messageData)
    }

object EmailReader extends ((Long, String, String, String, String, String) => EmailReader){
  
  implicit val emailReaderFormat = Json.format[EmailReader]

  def apply(tuple: (Long, String, String, String, String, String)):EmailReader = 
    EmailReader(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6)  
  
  implicit val getEmailReader = GetResult(r => 
    EmailReader(r.nextLong, r.nextString, r.nextString, r.nextString, r.nextString, r.nextString)
  )
}


package daos.reader

import models.{Email, Crew}
import slick.jdbc.GetResult
import play.api.libs.json._
import java.sql.Date

case class EmailReader(
  id: Long,
  senderUUID: String,
  senderName: String, 
  senderMail: String,
  senderCrewName: String,
  senderCrewId: String,
  subject: String,  
  messageData: String,
  date: Date,
  status: String,
  ) {
    def toEmail: Email = Email(  
      id,
      senderUUID,
      senderName, 
      senderMail,
      Crew(senderCrewId,senderCrewName,None,None),
      Array.empty[String],
      subject,  
      messageData,
      Some(date),
      status
      )
      def toEmail(recipients: Seq[String]): Email = Email(  
      id,
      senderUUID,
      senderName, 
      senderMail,
      Crew(senderCrewId,senderCrewName,None,None),
      recipients,
      subject,  
      messageData,
      Some(date),
      status
      )
    }

object EmailReader extends ((Long, String, String, String, String, String, String, String, Date, String) => EmailReader){
  
  implicit val emailReaderFormat = Json.format[EmailReader]

  def apply(tuple: (Long, String, String, String, String, String, String, String, Date, String)):EmailReader = 
    EmailReader(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, tuple._7, tuple._8, tuple._9, tuple._10)  
  
  implicit val getEmailReader = GetResult(r => 
    EmailReader(r.nextLong, r.nextString, r.nextString, r.nextString, r.nextString, r.nextString, r.nextString, r.nextString, r.nextDate, r.nextString)
  )
}


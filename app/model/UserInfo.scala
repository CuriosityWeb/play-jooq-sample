package model

import play.api.libs.json._

import java.time.LocalDate

final case class UserInfo(id: String, fName: String, lName: String, age: LocalDate)

object UserInfo {

  implicit val UserInfoFormat: OFormat[UserInfo] = Json.format[UserInfo]
}

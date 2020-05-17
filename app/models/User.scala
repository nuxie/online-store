package models

import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class User(id: Int, name: String, e_mail: String, tax_number: Option[Int])

object User {
  implicit val userFormat: OFormat[User] = Json.format[User]
}
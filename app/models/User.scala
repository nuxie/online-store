package models

import java.util.UUID

import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._

case class User(id: String = UUID.randomUUID.toString, email: String, firstName: String, lastName: String, role: String = "USER")

object User {
  implicit val userFormat: OFormat[User] = Json.format[User]
}

class UserTable(tag: Tag) extends Table[User](tag, "APPUSER") {
  def id = column[String]("ID", O.PrimaryKey, O.Unique)
  def email = column[String]("EMAIL", O.Unique)
  def firstName = column[String]("FIRST_NAME")
  def lastName = column[String]("LAST_NAME")
  def role = column[String]("ROLE")
  def * = (id, email, firstName, lastName, role) <> ((User.apply _).tupled, User.unapply)
}

case class UserPreview(id: String, email: String)
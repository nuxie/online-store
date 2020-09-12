package models

import java.util.UUID

import play.api.libs.json.Json
import slick.jdbc.SQLiteProfile.api._

case class User(id: String = UUID.randomUUID.toString,
                email: String,
                firstName: String,
                lastName: String,
                role: String = "USER")

object User {
  implicit val userFormat = Json.format[User]
}

class UserTable(tag: Tag) extends Table[User](tag, "AppUser") {
  def id = column[String]("Id", O.PrimaryKey, O.Unique)

  def email = column[String]("Email", O.Unique)

  def firstName = column[String]("FirstName")

  def lastName = column[String]("LastName")

  def role = column[String]("Role")

  def * = (id, email, firstName, lastName, role) <> ((User.apply _).tupled, User.unapply)
}

case class UserPreview(id: String, email: String)
package models

import play.api.libs.json.Json
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.{TableQuery, Tag}

case class PasswordInfoDb(hasher: String,
                          password: String,
                          salt: Option[String],
                          loginInfoId: String)

object PasswordInfoDb {
  implicit val passwordInfoFormat = Json.format[PasswordInfoDb]
}

class PasswordInfoTable(tag: Tag) extends Table[PasswordInfoDb](tag, "PasswordInfo") {
  def hasher = column[String]("Hasher")

  def password = column[String]("Password")

  def salt = column[Option[String]]("Salt")

  def loginInfoId = column[String]("LoginInfoId")

  def loginInfoFK = foreignKey("login_info_fk", loginInfoId, TableQuery[LoginInfoTable])(_.id)

  def * = (hasher, password, salt, loginInfoId) <> ((PasswordInfoDb.apply _).tupled, PasswordInfoDb.unapply)
}

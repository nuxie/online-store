package models

import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.{TableQuery, Tag}

case class PasswordInfoDb(hasher: String,
                          password: String,
                          salt: Option[String],
                          loginInfoId: String)

object PasswordInfoDb {
  implicit val passwordInfoFormat: OFormat[PasswordInfoDb] = Json.format[PasswordInfoDb]
}

class PasswordInfoTable(tag: Tag) extends Table[PasswordInfoDb](tag, "PASSWORDINFO") {
  def hasher = column[String]("HASHER")
  def password = column[String]("PASSWORD")
  def salt = column[Option[String]]("SALT")
  def loginInfoId = column[String]("LOGININFO_ID")
  def loginInfoFK = foreignKey("loginInfoFK", loginInfoId, TableQuery[LoginInfoTable])(_.id)
  def * = (hasher, password, salt, loginInfoId) <> ((PasswordInfoDb.apply _).tupled, PasswordInfoDb.unapply)
}

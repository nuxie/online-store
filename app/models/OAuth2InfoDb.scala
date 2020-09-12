package models

import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.{TableQuery, Tag}

case class OAuth2InfoDb(id: String,
                        accessToken: String,
                        tokenType: Option[String],
                        expiresIn: Option[Int],
                        refreshToken: Option[String],
                        loginInfoId: String)

object OAuth2InfoDb {
  implicit val oauth2InfoFormat: OFormat[OAuth2InfoDb] = Json.format[OAuth2InfoDb]
}

class OAuth2InfoTable(tag: Tag) extends Table[OAuth2InfoDb](tag, "OAUTH2INFO") {
  def id = column[String]("ID", O.PrimaryKey, O.Unique)
  def accessToken = column[String]("ACCESS_TOKEN")
  def tokenType = column[Option[String]]("TOKEN_TYPE")
  def expiresIn = column[Option[Int]]("EXPIRES_IN")
  def refreshToken = column[Option[String]]("REFRESH_TOKEN")
  def loginInfoId = column[String]("LOGININFO_ID")
  def loginInfoFK = foreignKey("loginInfoFK", loginInfoId, TableQuery[LoginInfoTable])(_.id)
  def * = (id, accessToken, tokenType, expiresIn, refreshToken, loginInfoId) <> ((OAuth2InfoDb.apply _).tupled, OAuth2InfoDb.unapply)
}
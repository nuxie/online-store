package models

import play.api.libs.json.Json
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.{TableQuery, Tag}

case class OAuth2InfoDb(id: String,
                        accessToken: String,
                        tokenType: Option[String],
                        expiresIn: Option[Int],
                        refreshToken: Option[String],
                        loginInfoId: String)

object OAuth2InfoDb {
  implicit val oauth2InfoFormat = Json.format[OAuth2InfoDb]
}

class OAuth2InfoTable(tag: Tag) extends Table[OAuth2InfoDb](tag, "OAuth2Info") {
  def id = column[String]("Id", O.PrimaryKey, O.Unique)

  def accessToken = column[String]("AccessToken")

  def tokenType = column[Option[String]]("TokenType")

  def expiresIn = column[Option[Int]]("ExpiresIn")

  def refreshToken = column[Option[String]]("RefreshToken")

  def loginInfoId = column[String]("LoginInfoId")

  def loginInfoFK = foreignKey("login_info_fk", loginInfoId, TableQuery[LoginInfoTable])(_.id)

  def * = (id, accessToken, tokenType, expiresIn, refreshToken, loginInfoId) <> ((OAuth2InfoDb.apply _).tupled, OAuth2InfoDb.unapply)
}